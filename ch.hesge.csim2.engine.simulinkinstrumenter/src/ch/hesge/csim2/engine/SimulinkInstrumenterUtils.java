package ch.hesge.csim2.engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.simulinkparser.SimulinkBlock;
import ch.hesge.csim2.simulinkparser.SimulinkModel;

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

		if (block.isParameter()) {
			// Trace parameters
			Console.writeLine(indentation + block.getName() + " = " + block.getValue());
		}
		else {
			
			// Trace block elements
			Console.writeLine(indentation + block.getNodeType() + " [" + block.getSourceLine() + "]");

			for (SimulinkBlock inputPort : block.getInputPorts()) {
				for (SimulinkBlock output : inputPort.getOutputs()) {
					Console.writeLine(indentation + "  inport:    " + inputPort.getName() + "[" + inputPort.getParent().getName() + "] => " + output.getName() + "[" + output.getParent().getName() + "]");
				}
			}

			for (SimulinkBlock outputPort : block.getOutputPorts()) {
				for (SimulinkBlock inputs : outputPort.getInputs()) {
					Console.writeLine(indentation + "  outport:   " + inputs.getName() + "[" + inputs.getParent().getName() + "] => " + outputPort.getName() + "[" + outputPort.getParent().getName() + "]");
				}
			}

			for (SimulinkBlock inputs : block.getInputs()) {
				Console.writeLine(indentation + "  input:     " + inputs.getName() + "[" + inputs.getParent().getName() + "]");
			}

			for (SimulinkBlock outputs : block.getOutputs()) {
				Console.writeLine(indentation + "  outputs:   " + outputs.getName() + "[" + outputs.getParent().getName() + "]");
			}

			for (SimulinkBlock child : block.getChildren()) {
				dumpBlock(child, indentation + "  ");
			}
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

		if (block.isParameter()) {
			// Save parameters
			writer.write(indentation + block.getName() + " " + block.getValue());
			writer.newLine();
		}
		else {
			// Save blocks
			writer.write(indentation + block.getNodeType() + " {");
			writer.newLine();

			for (SimulinkBlock child : block.getChildren()) {
				saveBlock(child, writer, indentation + "  ");
			}

			writer.write(indentation + "}");
			writer.newLine();
		}
	}
}
