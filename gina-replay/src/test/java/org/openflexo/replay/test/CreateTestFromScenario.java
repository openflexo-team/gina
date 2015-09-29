package org.openflexo.replay.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FilenameUtils;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class CreateTestFromScenario {
	private String input, output;
	private final String templatePath = "scenarii/tests/template";

	public CreateTestFromScenario(String input, String output) {
		this.input = input;
		this.output = output;
	}

	private boolean create() {
		ReplayTestConfiguration testConfiguration;
		File scenarioFile;
		try {
			testConfiguration = new ReplayTestConfiguration();
			scenarioFile = ((FileResourceImpl) ResourceLocator.locateResource(this.input)).getFile();
			testConfiguration.loadScenario(scenarioFile);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Can't load " + this.input);
			return false;
		}

		if (output.equals("auto")) {
			output = ((FileResourceImpl) ResourceLocator.locateSourceCodeResource("scenarii"))
					.getFile().getParentFile().getParentFile().getAbsolutePath()
					+ File.separator + "java" + File.separator + "org" + File.separator + "openflexo"
					+ File.separator + "replay" + File.separator + "tests" + File.separator;
			
			output += testConfiguration.getMainClassLauncher().getSimpleName() + "Test.java";
		}
		
		String className = FilenameUtils.removeExtension(new File(output).getName());

		System.out.println("Creating " + className + " (" + output + ") from " + input + "...");
		
		// Write file
		PrintWriter writer;
		try {
			writer = new PrintWriter(output, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Can't open " + this.output);
			return false;
		}
		
		BufferedReader br;
		File template = ((FileResourceImpl) ResourceLocator.locateResource(templatePath)).getFile();
		try {
			br = new BufferedReader(new FileReader(template));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Can't open " + this.input);
			return false;
		}
	    String line;
	    try {
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("%%RESOURCE_SCENARIO%%", this.input);
				line = line.replaceAll("%%CLASS_NAME%%", className);
				writer.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can't read line");
			return false;
		}
		writer.close();
		
		System.out.println("Creating successful !");
		System.out.println("Output : " + this.output);
		
		return true;
	}

	public static void main(String[] args) {
		String input = (args.length > 1 ? args[0] : "scenarii/last-scenario");
		String output = (args.length > 2 ? args[1] : "auto");

		CreateTestFromScenario creator = new CreateTestFromScenario(input, output);
		creator.create();
	}

}
