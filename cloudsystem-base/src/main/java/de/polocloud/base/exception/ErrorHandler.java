package de.polocloud.base.exception;

import de.polocloud.base.exception.matcher.impl.ErrorMatcher;
import de.polocloud.base.exception.matcher.Matcher;
import de.polocloud.base.exception.matcher.MatcherFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class ErrorHandler {

    private static ErrorHandler defaultInstance = null;

    private final Map<ErrorCodeIdentifier<?>, MatcherFactory<?>> errorCodeMap;

    private final List<ActionEntry> actions;
    private final List<ErrorAction> otherwiseActions;
    private final List<ErrorAction> alwaysActions;

    private ThreadLocal<ExceptionContext> localContext;

    private ErrorHandler parentErrorHandler;

    /**
     * Need a private constructor as we want new instances created
     * only via the {@link #create} methods.
     */
    private ErrorHandler() {
        this.actions = new ArrayList<>();
        this.otherwiseActions = new ArrayList<>();
        this.alwaysActions = new ArrayList<>();
        this.errorCodeMap = new ConcurrentHashMap<>();
        this.localContext = ThreadLocal.withInitial(ExceptionContext::new);
    }

    /**
     * Method for creating a ErrorHandler, which depends on
     * a previous ErrorHandler
     *
     * @param dependErrorHandler depending Error Handler
     */
    private ErrorHandler(ErrorHandler dependErrorHandler) {
        this();
        this.parentErrorHandler = dependErrorHandler;
    }

    /**
     * Creates an entire new ErrorHandler, without the default ErrorHandler
     *
     * @return a new Instance of the {@link ErrorHandler}
     */
    public static ErrorHandler createFresh() {
        return new ErrorHandler();
    }

    /**
     * Creates a new ErrorHandler instance depending on the {@link #defaultInstance()}
     * <p>
     * via {@link #ErrorHandler(ErrorHandler)}
     *
     * @return a new Instance of the {@link ErrorHandler}
     */
    public static ErrorHandler create() {
        return new ErrorHandler(defaultInstance());
    }

    /**
     * Get the main instance of the ErrorHandler
     * When not initialized, creating new Instance
     *
     * @return a new Instance of the {@link ErrorHandler}
     */
    public static synchronized ErrorHandler defaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new ErrorHandler();
        }
        return defaultInstance;
    }

    /**
     * Adds a certain action, on a certain ErrorCode
     *
     * @param matcher the ErrorCode
     * @param action  the Action, which will be executed if this error occurs
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler onError(Matcher matcher, @NotNull ErrorAction action) {
        if (matcher == null) {
            throw new IllegalArgumentException("Tried to register a 'onError' in ExceptionHandler, but the Matcher is null");
        }
        this.actions.add(new ActionEntry(matcher, action));
        return this;
    }

    /**
     * Adds a certain action, on a certain Exception in an Exception Class
     * <p>
     * In this method, the Class is the ErrorCode and doesn't have to register separately
     *
     * @param exceptionClass the Class when the exception happens
     * @param action         the Action, which will be executed if this error occurs
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler onError(Class<? extends Exception> exceptionClass, @NotNull ErrorAction action) {
        if (exceptionClass == null) {
            throw new IllegalArgumentException("Tried to register a 'onError' in ExceptionHandler, but the ExceptionClass is null");
        }
        this.actions.add(new ActionEntry(new ErrorMatcher(exceptionClass), action));
        return this;
    }

    /**
     * Adds a certain action, on a certain Exception in an Exception Class
     *
     * @param errorCode the ErrorCode
     * @param action    the Action, which will be executed if this error occurs
     * @param <T>       the Object of an ErrorCode, this can be everything from a {@link String} to an {@link java.io.FileReader}
     * @return the Instance of the current {@link ErrorHandler}
     */
    public <T> ErrorHandler onError(T errorCode, ErrorAction action) {
        if (errorCode == null) {
            throw new IllegalArgumentException("Tried to register a 'onError' in ExceptionHandler, but the ErrorCode is null");
        }

        final var matcherFactory = getMatcherFactoryFromErrorCode(errorCode);
        if (matcherFactory == null) {
            System.out.println("Tried to register a 'onError' in ExceptionHandler, but the ErrorCode is not registered. Skipped...");
            return this;
        }

        this.actions.add(new ActionEntry(matcherFactory.build(errorCode), action));
        return this;
    }

    /**
     * Will be called if no ErrorCode was found for the {@link Throwable}
     * <p>
     * Is ignorable by the {@link #ignoreDefaults()} or the {@link #ignoreAll()}
     *
     * @param action the Action, which will be executed if this error occurs
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler orElse(@NotNull ErrorAction action) {
        this.otherwiseActions.add(action);
        return this;
    }

    /**
     * Will be always called
     * <p>
     * Is ignorable by the {@link #ignoreDefaults()} or the {@link #ignoreAll()}
     *
     * @param action the Action, which will be executed if this error occurs
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler doAlways(@NotNull ErrorAction action) {
        this.alwaysActions.add(action);
        return this;
    }

    /**
     * Ignores the upon following {@link ErrorAction}
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreFollowing() {
        if (this.localContext != null) {
            this.localContext.get().skipFollowing = true;
        }
        return this;
    }

    /**
     * Ignores all the Always {@link ErrorAction} actions
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreAlways() {
        if (this.localContext != null) {
            this.localContext.get().skipAlways = true;
        }
        return this;
    }

    /**
     * Ignores all the orElse {@link ErrorAction} actions
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreDefaults() {
        if (this.localContext != null) {
            this.localContext.get().skipDefaults = true;
        }
        return this;
    }

    /**
     * Ignores the orElse, Always, upon Following {@link ErrorAction} actions
     * <p>
     * Via {@link #ignoreDefaults()} {@link #ignoreAlways()} () {@link #ignoreFollowing()} ()
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreAll() {
        return this.ignoreDefaults().ignoreAlways().ignoreFollowing();
    }

    /**
     * Handles a {@link Throwable} by checking if an action is for this {@link Throwable}
     * if no action found, calls the all the orElse actions (if not ignored)
     * calls the always Actions (if not ignored)
     *
     * @param error   the {@link Throwable error}
     * @param context the LocalThreadContext for getting the {@link ExceptionContext} from
     */
    protected void handle(Throwable error, ThreadLocal<ExceptionContext> context) {
        if (error == null)
            throw new IllegalArgumentException("Tried to 'handle' in ExceptionHandler, but the Error (Throwable) is null");

        this.localContext = context;
        final var ctx = this.localContext.get();

        for (final var actionEntry : this.actions) {
            if (ctx.skipFollowing) break;
            if (actionEntry.matcher().matches(error)) {
                actionEntry.action().execute(error, this);
                ctx.handled = true;
            }
        }

        if (!ctx.handled && !otherwiseActions.isEmpty()) {
            for (final var action : this.otherwiseActions) {
                action.execute(error, this);
                ctx.handled = true;
            }
        }

        if (!ctx.skipAlways) {
            for (final var action : this.alwaysActions) {
                action.execute(error, this);
                ctx.handled = true;
            }
        }

        if (this.parentErrorHandler != null && !ctx.skipDefaults) {
            this.parentErrorHandler.handle(error, this.localContext);
        }
    }

    /**
     * Runs a {@link Callable} can be Method or something else.
     * This method is useful, if the other method could throw an Exception
     * <p>
     * When the Callable throws an Exception, returns null
     * <p>
     * Via {@link #runOrDefault(Callable, Object, boolean)}
     *
     * @param callable the Callable which will be called
     * @param <T>      the Class of the Callable
     * @return the Object, which was returned of the Callable
     */
    public <T> T run(Callable<T> callable) {
        return this.runOrDefault(callable, null, true);
    }

    /**
     * Runs a {@link Callable} can be Method or something else.
     * This method is useful, if the other method could throw an Exception
     * <p>
     * When the Callable throws an Exception, returns the defaultValue
     * <p>
     * Via {@link #runOrDefault(Callable, Object, boolean)}
     *
     * @param callable     the Callable which will be called
     * @param <T>          the Class of the Callable
     * @param defaultValue the defaultValue, which will be returned if the
     *                     callable throws an Exception
     * @return the Object, which was returned of the Callable
     */
    public <T> T runOrDefault(Callable<T> callable, T defaultValue) {
        return this.runOrDefault(callable, defaultValue, true);
    }

    /**
     * Runs a {@link Callable} can be Method or something else.
     * This method is useful, if the other method could throw an Exception
     * <p>
     * When the Callable throws an Exception, returns the defaultValue
     *
     * @param callable        the Callable which will be called
     * @param <T>             the Class of the Callable
     * @param defaultValue    the defaultValue, which will be returned if the
     *                        callable throws an Exception
     * @param reportException decides, if the Exception will be handled with
     *                        the own ErrorHandler
     * @return the Object, which was returned of the Callable
     */
    public <T> T runOrDefault(Callable<T> callable, T defaultValue, boolean reportException) {
        try {
            return callable.call();
        } catch (Exception exception) {
            if (reportException) {
                handle(exception, this.localContext);
            }
        }
        return defaultValue;
    }

    /**
     * Runs one {@link Callable}, when this fails, it tries
     * the next, until one success, this value will be returned
     * <p>
     * Via {@link #runOrDefault(Callable, Object, boolean)}
     *
     * @param defaultValue the defaultValue, which will be
     *                     returned if all actions throw an
     *                     exception
     * @param actions      the actions, that will be tired to call
     * @param <T>          the Class of the Callable
     * @return the Object, which was returned of the Callable
     */
    public <T> T runUntilSuccess(T defaultValue, List<Callable<T>> actions) {
        for (final var action : actions) {
            final var execution = runOrDefault(action, null, false);
            if (execution != null) {
                return execution;
            }
        }
        return defaultValue;
    }

    /**
     * Runs one {@link Callable}, when this fails, it tries
     * the next, until one success, this value will be returned
     * <p>
     * Via {@link #runUntilSuccess(Object, List)}
     *
     * @param defaultValue the defaultValue, which will be
     *                     returned if all actions throw an
     *                     exception
     * @param tries        the actions, that will be tired to call
     * @param <T>          the Class of the Callable
     * @return the Object, which was returned of the Callable
     */
    @SafeVarargs
    public final <T> T runUntilSuccess(T defaultValue, @NotNull Callable<T>... tries) {
        return this.runUntilSuccess(defaultValue, Arrays.stream(tries).toList());
    }

    /**
     * Runs a {@link Callable} can be Method or something else.
     * This method is useful, if the other method could throw an Exception
     * Doesn't return anything
     *
     * @param callable the Callable which will be called
     */
    public void runOnly(Callable<Void> callable) {
        try {
            callable.call();
        } catch (Exception exception) {
            this.handle(exception, this.localContext);
        }
    }

    /**
     * Takes a {@link Throwable} and handles it
     * <p>
     * Via {@link #handle(Throwable, ThreadLocal)}
     *
     * @param error the {@link Throwable error}
     */
    public void handle(Throwable error) {
        this.handle(error, this.localContext);
    }

    /**
     * Registers a ErrorCode, with a specific {@link MatcherFactory}
     *
     * @param errorCode      the specific ErrorCode (should be used in the {@link #onError(Object, ErrorAction)}
     * @param matcherFactory the matcher code
     * @param actions        the actions, which will be automatically added to this ErrorCode (via {@link #onError(Object, ErrorAction)}
     * @param <T>            the Object for the ErrorCode and the MatcherFactory
     * @return the Instance of the current {@link ErrorHandler}
     */
    public <T> ErrorHandler bindCode(T errorCode, MatcherFactory<? super T> matcherFactory, ErrorAction... actions) {
        this.errorCodeMap.put(new ErrorCodeIdentifier<>(errorCode), matcherFactory);

        for (final var action : actions) this.onError(errorCode, action);

        return this;
    }

    /**
     * Registers a ErrorCode, with a specific {@link MatcherFactory}
     *
     * @param errorCodeClass the specific Class which will be registered as an ErrorCode
     * @param matcherFactory the matcher code
     * @param actions        the actions, which will be automatically added to this ErrorCode (via {@link #onError(Object, ErrorAction)}
     * @param <T>            the Object for the ErrorCode and the MatcherFactory
     * @return the Instance of the current {@link ErrorHandler}
     */
    public <T> ErrorHandler bindClass(Class<T> errorCodeClass, MatcherFactory<? super T> matcherFactory, ErrorAction... actions) {
        this.errorCodeMap.put(new ErrorCodeIdentifier<>(errorCodeClass), matcherFactory);

        for (final var action : actions) this.onError(errorCodeClass, action);

        return this;
    }

    /**
     * Registers a UncaughtExceptionHandler for every Thread
     * <p>
     * Via {@link Thread#setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)}
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler registerDefaultThreadExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> handle(e));
        return this;
    }

    /**
     * Gets a Matcher by its ErrorCode (checks the ErrorCode, or the ErrorCode-Class)
     *
     * @param errorCode the ErrorCode for searching the matcher
     * @param <T>       the Object of the ErrorCode
     * @return the found {@link MatcherFactory}
     */
    protected <T> MatcherFactory<? super T> getMatcherFactoryFromErrorCode(T errorCode) {
        var matcherFactory = this.errorCodeMap.get(new ErrorCodeIdentifier<>(errorCode));
        if (matcherFactory != null) {
            return (MatcherFactory<? super T>) matcherFactory;
        }
        matcherFactory = this.errorCodeMap.get(new ErrorCodeIdentifier<>(errorCode.getClass()));
        return matcherFactory != null ? (MatcherFactory<? super T>) matcherFactory : (parentErrorHandler != null ? parentErrorHandler.getMatcherFactoryFromErrorCode(errorCode) : null);
    }

    /**
     * Clears all Lists and values of the {@link ErrorHandler}
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler clearErrorHandler() {
        this.actions.clear();
        this.errorCodeMap.clear();
        this.otherwiseActions.clear();
        this.alwaysActions.clear();
        if (this.localContext != null) this.localContext.get().clear();
        return this;
    }

    /**
     * Get all ErrorCodes, by it's ErrorCode or it's ErrorCode-Class
     *
     * @return a Lists with all ErrorCodes
     */
    public List<Object> getRegisteredErrorCodes() {
        return this.errorCodeMap.keySet().stream()
            .map(errorCodeIdentifier -> errorCodeIdentifier.errorCode == null ? errorCodeIdentifier.errorCodeClass :
                errorCodeIdentifier.errorCode).toList();
    }

    private static class ExceptionContext {
        boolean handled;
        boolean skipDefaults = false;
        boolean skipFollowing = false;
        boolean skipAlways = false;

        /**
         * Clears this Instance
         */
        void clear() {
            this.skipDefaults = false;
            this.skipFollowing = false;
            this.skipAlways = false;
        }
    }

    private static final class ErrorCodeIdentifier<T> {
        private T errorCode;
        private Class<T> errorCodeClass;

        ErrorCodeIdentifier(T errorCode) {
            this.errorCode = errorCode;
        }

        ErrorCodeIdentifier(Class<T> errorCodeClass) {
            this.errorCodeClass = errorCodeClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final var that = (ErrorCodeIdentifier<?>) o;
            return Objects.equals(this.errorCode, that.errorCode) && Objects.equals(this.errorCodeClass, that.errorCodeClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.errorCode, this.errorCodeClass);
        }
    }

}
