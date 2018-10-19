package com.shardbytes.music.server;

import java.lang.instrument.Instrumentation;

public class InstrumentationAgent{

	private static volatile Instrumentation globalInstrumentation;

	public static void premain(final String agentArguments, final Instrumentation instrumentation){
		globalInstrumentation = instrumentation;
	}

	public static long sizeOf(final Object object){
		if(globalInstrumentation == null){
			throw new IllegalStateException("Instrumentation agent not initialized.");
		}

		return globalInstrumentation.getObjectSize(object);

	}

}
