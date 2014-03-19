package com.hanacek.android.utilLib.util;

import android.app.Activity;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Log {
    public static String defaultTag = "UNSPECIFIED";
    
    private static LinkedList<LogRecord> logBuffer;
    
    public static final int VERBOSE_LEVEL = 0;
    public static final int DEBUG_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARNING_LEVEL = 3;
    public static final int SEVERE_LEVEL = 4;
    
    private static final String[] LEVEL_NAMES = new String[] { "VERBOSE", "DEBUG", "INFO", "WARN", "SEVERE" };
    
    private static final int releaseLogLevel = SEVERE_LEVEL;
    private static final int emulatorLoglevel = DEBUG_LEVEL;
    
    private static int loglevel = releaseLogLevel;
    private static int logInMemorylevel = releaseLogLevel;
    
    private static int maxInMemoryBufferLength;
    
    public final static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    
    public static void initialize(String defaultTag, int maxInMemoryBufferLength, int logLevel, int inMemoryLogLevel) {
    	Log.loglevel = logLevel;
    	Log.logInMemorylevel = inMemoryLogLevel;
    	logBuffer = new LinkedList<LogRecord>();
    	Log.maxInMemoryBufferLength = maxInMemoryBufferLength;
    	Log.defaultTag = defaultTag;
    }
    
    public static void initialize(String defaultTag, int maxInMemoryBufferLength) {
    	// did get rid of emulator detector as we would need another permission
//        int loglevel = EmulatorDetector.isEmulator(context) ? emulatorLoglevel : releaseLogLevel;

    	initialize(defaultTag, maxInMemoryBufferLength, emulatorLoglevel, emulatorLoglevel);
    }
    
    private static TextView view;
    private static Activity activity;
    
    public static void setView(TextView view, Activity activity) {
        Log.view = view;
        Log.activity = activity;
    }
    
    private static String formatMessage(long timestamp, String message) {
    	return formatMessage(timestamp, message, Thread.currentThread().getName());
    }
    
    private static String formatMessage(long timestamp, String message, String threadName) {
    	return "[" + dateFormat.format(new Date(timestamp)) + ", " + threadName + "]: " + message;
    }

    private static void appendToBuffer(int level, long timestamp, String tag, String message, Throwable throwable) {
    	if ( maxInMemoryBufferLength > 0 ) {    	
	    	final LogRecord record = new LogRecord(level, timestamp, tag, message, Thread.currentThread().getName(), throwable);
	    	
	    	if (view != null) {
	    	    activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(record.getFormattedRecord() + "\n" + view.getText());
                    }
                });
            }
	    	
	    	synchronized (Log.class) {
		        logBuffer.addFirst(record);
		        if ( logBuffer.size() > maxInMemoryBufferLength )
		            logBuffer.removeLast();				
			}
    	}
    }
        
    private static void logInMemory(int level, long timestamp, String tag, String message, Throwable throwable) {
    	if ( logInMemorylevel > level )
    		return;
    	
    	appendToBuffer(level, timestamp, tag, message, throwable);
    }
    
    public static void verbose(String tag, String message, Throwable throwable) {
    	long timestamp = System.currentTimeMillis();
    	logInMemory(VERBOSE_LEVEL, timestamp, tag, message, throwable);
    	
    	if ( loglevel > VERBOSE_LEVEL )
    		return;

    	if ( throwable != null ) {
    		android.util.Log.v(tag, formatMessage(timestamp, message), throwable);
    	} else {
    		android.util.Log.v(tag, formatMessage(timestamp, message));
    	}
    }

    public static void verbose(String message, Throwable throwable) {
    	verbose(defaultTag, message, throwable);
    }

    public static void verbose(String message) {
    	verbose(defaultTag, message, null);
    }


    public static void debug(String tag, String message, Throwable throwable) {
    	long timestamp = System.currentTimeMillis();
    	logInMemory(DEBUG_LEVEL, timestamp, tag, message, throwable);
    	
    	if ( loglevel > DEBUG_LEVEL )
    		return;

    	if ( throwable != null ) {
    		android.util.Log.d(tag, formatMessage(timestamp, message), throwable);
    	} else {
    		android.util.Log.d(tag, formatMessage(timestamp, message));
    	}
    }

    public static void debug(String message, Throwable throwable) {
    	debug(defaultTag, message, throwable);
    }

    public static void debug(String message) {
    	debug(defaultTag, message, null);
    }

    public static void info(String tag, String message, Throwable throwable) {
    	long timestamp = System.currentTimeMillis();
    	logInMemory(INFO_LEVEL, timestamp, tag, message, throwable);

    	if ( loglevel > INFO_LEVEL )
    		return;

    	if ( throwable != null ) {
    		android.util.Log.i(tag, formatMessage(timestamp, message), throwable);
    	} else {
    		android.util.Log.i(tag, formatMessage(timestamp, message));
    	}
    }

    public static void info(String message, Throwable throwable) {
    	info(defaultTag, message, throwable);
    }

    public static void info(String message) {
    	info(defaultTag, message, null);
    }

    
    public static void warn(String tag, String message, Throwable throwable) {
    	long timestamp = System.currentTimeMillis();
    	logInMemory(WARNING_LEVEL, timestamp, tag, message, throwable);
    	
    	if ( loglevel > WARNING_LEVEL )
    		return;

    	if ( throwable != null ) {
    		android.util.Log.w(tag, formatMessage(timestamp, message), throwable);
    	} else {
    		android.util.Log.w(tag, formatMessage(timestamp, message));
    	}
    }

    public static void warn(String message, Throwable throwable) {
    	warn(defaultTag, message, throwable);
    }

    public static void warn(String message) {
    	warn(defaultTag, message, null);
    }

    public static void error(String tag, String message, Throwable throwable) {
    	long timestamp = System.currentTimeMillis();
    	logInMemory(SEVERE_LEVEL, timestamp, tag, message, throwable);
    	
    	if ( loglevel > SEVERE_LEVEL )
    		return;

    	if ( throwable != null ) {
    		android.util.Log.e(tag, formatMessage(timestamp, message), throwable);
    	} else {
    		android.util.Log.e(tag, formatMessage(timestamp, message));
    	}
    }

    public static void error(String message, Throwable throwable) {
    	error(defaultTag, message, throwable);
    }

    public static void error(Throwable throwable) {
    	error(defaultTag, "Exception: " + throwable.getClass().getSimpleName(), throwable);
    }
    
    public static void error(String message) {
    	error(defaultTag, message, null);
    }
    
    public static List<LogRecord> getLogBuffer() {
    	synchronized (Log.class) {
    		return new LinkedList<LogRecord>(logBuffer);
    	}
    }
    
    public static String getFormattedBuffer() {
    	List<LogRecord> records = Log.getLogBuffer();
		StringBuilder sb = new StringBuilder();
		for (LogRecord lr : records) {
			sb.append(lr.getFormattedRecord()).append("\n");
		}
		return sb.toString();
    }

    public static String appendStackTrace(StackTraceElement[] elements) {
		try {
			StringBuilder result = new StringBuilder();
			for (StackTraceElement element : elements) {
				result.append("\t").append(element.toString()).append("\n");
			}		
			return result.toString();
		} catch ( Exception e ) {
			return "Failed to serialize report. Error: " + e.getClass() + " ->" + e.getMessage();
		}
    }
    
    public static class LogRecord {
    	final int level;
    	final long timestamp;
    	final String tag;
    	final String message;
    	final String threadName;
    	final Throwable throwable;
    	
		public LogRecord(int level, long timestamp, String tag, String message, String threadName, Throwable throwable) {
			this.level = level;
			this.timestamp = timestamp;
			this.tag = tag;
			this.message = message;
			this.threadName = threadName;
			this.throwable = throwable;
		}

		public int getLevel() {
			return level;
		}
		
		public String getLevelName() {
			return LEVEL_NAMES[level];
		}

		public long getTimestamp() {
			return timestamp;
		}

		public String getTag() {
			return tag;
		}

		public String getMessage() {
			return message;
		}
		
		public String getThreadName() {
			return threadName;
		}

		public Throwable getThrowable() {
			return throwable;
		}						
		
		public String getFormattedRecord() {
			StringBuilder builder = new StringBuilder();
			builder.append(tag + "." + getLevelName() + ": " + formatMessage(timestamp, message, threadName) + (throwable == null ? "" : " " + throwable.getMessage()));
			if (throwable != null) {
				builder.append("\n" + appendStackTrace(throwable.getStackTrace()));
			}
			return builder.toString();
		}
    }

    // ------- OPTIONAL LOGS

    private static List<String> registeredInstances = new ArrayList<String>();

    public static void registerInstance(String instance) {
        registeredInstances.add(instance);
    }

    public static void debug(String instance, String message) {
        if (registeredInstances.contains(instance)) {
            debug(defaultTag, "*"+instance.toUpperCase()+"* - " + message, null);
        }
    }

    public static void error(String instance, String message) {
        if (registeredInstances.contains(instance)) {
            error(defaultTag, "*"+instance.toUpperCase()+"* - " + message, null);
        }
    }
}