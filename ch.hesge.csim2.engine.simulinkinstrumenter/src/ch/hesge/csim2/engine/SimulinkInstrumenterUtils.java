package ch.hesge.csim2.engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.hesge.csim2.core.simulink.SimulinkBlock;
import ch.hesge.csim2.core.simulink.SimulinkModel;
import ch.hesge.csim2.core.utils.Console;

public class SimulinkInstrumenterUtils {

	/**
	 * Dump a model with all its block
	 * 
	 * @param model
	 *            the model to dump
	 */
	public static void dumpModel(SimulinkModel model) {

		if (model != null) {
			for (SimulinkBlock block : model.getRoot().getChildren()) {
				dumpBlock(block, "");
			}
		}
	}

	/**
	 * Dump nicely a block and all its children.
	 * 
	 * @param block
	 * @param indentation
	 */
	private static void dumpBlock(SimulinkBlock block, String indentation) {

		// Trace block elements
		Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + block.getNodeType() + " [" + block.getSourceLine() + "]");

		// Trace parameters
		for (SimulinkBlock param : block.getParameters()) {
			Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + param.getName() + " = " + param.getValue());
		}
		
		// Trace input ports
		for (SimulinkBlock inputPort : block.getInputPorts()) {
			for (SimulinkBlock output : inputPort.getOutputs()) {
				Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + "  inport:    " + inputPort.getName() + "[" + inputPort.getParent().getName() + "] => " + output.getName() + "[" + output.getParent().getName() + "]");
			}
		}

		// Trace output ports
		for (SimulinkBlock outputPort : block.getOutputPorts()) {
			for (SimulinkBlock inputs : outputPort.getInputs()) {
				Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + "  outport:   " + inputs.getName() + "[" + inputs.getParent().getName() + "] => " + outputPort.getName() + "[" + outputPort.getParent().getName() + "]");
			}
		}

		// Trace inputs ports
		for (SimulinkBlock inputs : block.getInputs()) {
			Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + "  input:     " + inputs.getName() + "[" + inputs.getParent().getName() + "]");
		}

		// Trace outputs ports
		for (SimulinkBlock outputs : block.getOutputs()) {
			Console.writeInfo(SimulinkInstrumenterUtils.class, indentation + "  outputs:   " + outputs.getName() + "[" + outputs.getParent().getName() + "]");
		}

		// Trace children
		for (SimulinkBlock child : block.getChildren()) {
			dumpBlock(child, indentation + "  ");
		}
	}

	/**
	 * Save a simulink model in a file, with all its blocks
	 * 
	 * @param model
	 * @param filepath
	 * @throws IOException
	 */
	public static void save(SimulinkModel model, String filepath) throws IOException {

		if (model != null && filepath != null) {
			
			// Retrieve absolute path
			Path path = Paths.get(filepath).toAbsolutePath().normalize();

			// Create a file writer
			BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset());

			// Start writing file
			try {
				for (SimulinkBlock block : model.getRoot().getChildren()) {
					saveBlock(block, writer, "");
				}
			}
			finally {
				if (writer != null)
					writer.flush();
			}
		}
	}

	/**
	 * Write all block information in a stream.
	 * 
	 * @param block
	 * @param writer
	 * @param indentation
	 * @throws IOException
	 */
	private static void saveBlock(SimulinkBlock block, BufferedWriter writer, String indentation) throws IOException {

		// Save blocks
		writer.write(indentation + block.getNodeType() + " {");
		writer.newLine();

		// Save parameters
		for (SimulinkBlock param : block.getParameters()) {
			writer.write(indentation + param.getName() + " " + param.getValue());
			writer.newLine();
		}

		// Save children
		for (SimulinkBlock child : block.getChildren()) {
			saveBlock(child, writer, indentation + "  ");
		}

		writer.write(indentation + "}");
		writer.newLine();
	}
}
