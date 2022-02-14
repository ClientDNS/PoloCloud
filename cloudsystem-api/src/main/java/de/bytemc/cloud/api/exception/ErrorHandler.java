package de.bytemc.cloud.api.exception;

import de.bytemc.cloud.api.exception.matcher.Matcher;
import de.bytemc.cloud.api.exception.matcher.MatcherFactory;
import de.bytemc.cloud.api.exception.matcher.impl.ErrorMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ErrorHandler {

    private static ErrorHandler defaultInstance = null;

    private final Map<ErrorCodeIdentifier, MatcherFactory> errorCodeMap;

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
     * Method for creating a de.ipommes.exceptionhandler.ErrorHandler, which depends on
     * a previous de.ipommes.exceptionhandler.ErrorHandler
     *
     * @param dependErrorHandler depending Error Handler
     */
    private ErrorHandler(ErrorHandler dependErrorHandler) {
        this();
        this.parentErrorHandler = dependErrorHandler;
    }

    /**
     * Creates an entire new de.ipommes.exceptionhandler.ErrorHandler, without the default de.ipommes.exceptionhandler.ErrorHandler
     *
     * @return a new Instance of the {@link ErrorHandler}
     */
    public static ErrorHandler createFresh() {
        return new ErrorHandler();
    }

    /**
     * Creates a new de.ipommes.exceptionhandler.ErrorHandler instance depending on the {@link #defaultInstance()}
     * <p>
     * via {@link #ErrorHandler(ErrorHandler)}
     *
     * @return a new Instance of the {@link ErrorHandler}
     */
    public static ErrorHandler create() {
        return new ErrorHandler(defaultInstance());
    }

    /**
     * Get the main instance of the de.ipommes.exceptionhandler.ErrorHandler
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
            throw new IllegalArgumentException("Tried to register a 'onError' in ExceptionHandler, but the de.ipommes.exceptionhandler.matcher.Matcher is null");
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
        actions.add(new ActionEntry(new ErrorMatcher(exceptionClass), action));
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

        MatcherFactory<? super T> matcherFactory = getMatcherFactoryFromErrorCode(errorCode);
        if (matcherFactory == null) {
            System.out.println("Tried to register a 'onError' in ExceptionHandler, but the ErrorCode is not registered. Skipped...");
            return this;
        }

        actions.add(new ActionEntry(matcherFactory.build(errorCode), action));
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
        otherwiseActions.add(action);
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
        alwaysActions.add(action);
        return this;
    }

    /**
     * Ignores the upon following {@link ErrorAction}
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreFollowing() {
        if (localContext != null) {
            localContext.get().skipFollowing = true;
        }
        return this;
    }

    /**
     * Ignores all the Always {@link ErrorAction} actions
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreAlways() {
        if (localContext != null) {
            localContext.get().skipAlways = true;
        }
        return this;
    }

    /**
     * Ignores all the orElse {@link ErrorAction} actions
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler ignoreDefaults() {
        if (localContext != null) {
            localContext.get().skipDefaults = true;
        }
        return this;
    }

    /**
     * Ignores the orElse, Always, upon Following {@link ErrorAction} actions
     * <p>
     * Via {@link #ignoreDefaults()} {@link #ignoreAlways()} ()} {@link #ignoreFollowing()} ()}
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

        localContext = context;

        ExceptionContext ctx = localContext.get();

        for (ActionEntry actionEntry : actions) {
            if (ctx.skipFollowing) break;
            if (actionEntry.matcher().matches(error)) {
                actionEntry.action().execute(error, this);
                ctx.handled = true;
            }
        }

        if (!ctx.handled && !otherwiseActions.isEmpty()) {
            for (ErrorAction action : otherwiseActions) {
                action.execute(error, this);
                ctx.handled = true;
            }
        }

        if (!ctx.skipAlways) {
            for (ErrorAction action : alwaysActions) {
                action.execute(error, this);
                ctx.handled = true;
            }
        }

        if (parentErrorHandler != null && !ctx.skipDefaults) {
            parentErrorHandler.handle(error, localContext);
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
        return runOrDefault(callable, null, true);
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
        return runOrDefault(callable, defaultValue, true);
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
     *                        the own de.ipommes.exceptionhandler.ErrorHandler
     * @return the Object, which was returned of the Callable
     */
    public <T> T runOrDefault(Callable<T> callable, T defaultValue, boolean reportException) {
        try {
            return callable.call();
        } catch (Exception exception) {
            if (reportException) {
                handle(exception, localContext);
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
        for (Callable<T> action : actions) {
            T execution = runOrDefault(action, null, false);
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
    public <T> T runUntilSuccess(T defaultValue, @NotNull Callable<T>... tries) {
        return runUntilSuccess(defaultValue, Arrays.stream(tries).toList());
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
            handle(exception, localContext);
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
        this.handle(error, localContext);
    }

    /**
     * Registers a ErrorCode, with a specific {@link MatcherFactory}
     *
     * @param errorCode      the specific ErrorCode (should be used in the {@link #onError(Object, ErrorAction)}
     * @param matcherFactory the matcher code
     * @param actions        the actions, which will be automatically added to this ErrorCode (via {@link #onError(Object, ErrorAction)}
     * @param <T>            the Object for the ErrorCode and the de.ipommes.exceptionhandler.matcher.MatcherFactory
     * @return the Instance of the current {@link ErrorHandler}
     */
    public <T> ErrorHandler bindCode(T errorCode, MatcherFactory<? super T> matcherFactory, ErrorAction... actions) {
        errorCodeMap.put(new ErrorCodeIdentifier<>(errorCode), matcherFactory);

        for (ErrorAction action : actions) {
            onError(errorCode, action);
        }

        return this;
    }

    /**
     * Registers a ErrorCode, with a specific {@link MatcherFactory}
     *
     * @param errorCodeClass the specific Class which will be registered as an ErrorCode
     * @param matcherFactory the matcher code
     * @param actions        the actions, which will be automatically added to this ErrorCode (via {@link #onError(Object, ErrorAction)}
     * @param <T>            the Object for the ErrorCode and the de.ipommes.exceptionhandler.matcher.MatcherFactory
     * @return the Instance of the current {@link ErrorHandler}
     */
    public <T> ErrorHandler bindClass(Class<T> errorCodeClass, MatcherFactory<? super T> matcherFactory, ErrorAction... actions) {
        errorCodeMap.put(new ErrorCodeIdentifier<>(errorCodeClass), matcherFactory);

        for (ErrorAction action : actions) {
            onError(errorCodeClass, action);
        }

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
     * Gets a de.ipommes.exceptionhandler.matcher.Matcher by its ErrorCode (checks the ErrorCode, or the ErrorCode-Class)
     *
     * @param errorCode the ErrorCode for searching the matcher
     * @param <T>       the Object of the ErrorCode
     * @return the found {@link MatcherFactory}
     */
    protected <T> MatcherFactory<? super T> getMatcherFactoryFromErrorCode(T errorCode) {
        MatcherFactory<T> matcherFactory;
        matcherFactory = errorCodeMap.get(new ErrorCodeIdentifier<>(errorCode));

        if (matcherFactory != null) {
            return matcherFactory;
        }

        matcherFactory = errorCodeMap.get(new ErrorCodeIdentifier(errorCode.getClass()));

        return matcherFactory != null ? matcherFactory : (parentErrorHandler != null ? parentErrorHandler.getMatcherFactoryFromErrorCode(errorCode) : null);
    }

    /**
     * Clears all Lists and values of the {@link ErrorHandler}
     *
     * @return the Instance of the current {@link ErrorHandler}
     */
    public ErrorHandler clearErrorHandler() {
        actions.clear();
        errorCodeMap.clear();
        otherwiseActions.clear();
        alwaysActions.clear();
        if (localContext != null) {
            localContext.get().clear();
        }
        return this;
    }

    /**
     * Get all ErrorCodes, by it's ErrorCode or it's ErrorCode-Class
     *
     * @return a Lists with all ErrorCodes
     */
    public List<Object> getRegisteredErrorCodes() {
        return this.errorCodeMap.keySet().stream().map(errorCodeIdentifier -> errorCodeIdentifier.errorCode == null ? errorCodeIdentifier.errorCodeClass : errorCodeIdentifier.errorCode).collect(Collectors.toList());
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
            skipDefaults = false;
            skipFollowing = false;
            skipAlways = false;
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
            ErrorCodeIdentifier<?> that = (ErrorCodeIdentifier<?>) o;
            return Objects.equals(errorCode, that.errorCode) && Objects.equals(errorCodeClass, that.errorCodeClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(errorCode, errorCodeClass);
        }
    }

}
