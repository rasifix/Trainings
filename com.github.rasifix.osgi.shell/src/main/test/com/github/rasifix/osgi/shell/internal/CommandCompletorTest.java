package com.github.rasifix.osgi.shell.internal;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.Completor;
import jline.SimpleCompletor;

import org.junit.Before;
import org.junit.Test;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

public class CommandCompletorTest {
	
	private CommandCompletor completor;
	
	@Before
	public void setup() {
		TestCommandRegistry registry = new TestCommandRegistry();
		registry.registerCommand("list", new SimpleCompletor(new String[] { "alpha", "beta", "gamma" }));
		registry.registerCommand("load", new SimpleCompletor(new String[] { "eins", "zwei", "drei" }));
		registry.registerCommand("undo", new SimpleCompletor(new String[] { "foo", "doo", "baa" }));
		registry.registerCommand("unregister", new SimpleCompletor(new String[] { "regi", "fegi", "hegi" }));
		registry.registerCommand("store", new SimpleCompletor(new String[] { "blub", "flub", "yup" }));
		completor = new CommandCompletor(registry);
	}
	
	@Test
	public void testCommandCompletion() throws Exception {
		List<String> candidates = complete("l", 1, 0);
		assertEquals(2, candidates.size());
		assertEquals("list", candidates.get(0));
		assertEquals("load", candidates.get(1));
	}
	
	@Test
	public void testArgumentCompletion() throws Exception {
		List<String> candidates = complete("load ", 5, 5);
		assertEquals(3, candidates.size());
		assertEquals("drei", candidates.get(0));
		assertEquals("eins", candidates.get(1));
		assertEquals("zwei", candidates.get(2));
	}
	
	@Test
	public void testArgumentCompletionInProgress() throws Exception {
		List<String> candidates = complete("undo f", 6, 5);
		assertEquals(1, candidates.size());
		assertEquals("foo ", candidates.get(0));
	}

	private List<String> complete(String buffer, int cursor, int expectedPosition) {
		List<String> candidates = new LinkedList<String>();
		int position = completor.complete(buffer, cursor, candidates);
		assertEquals(expectedPosition, position);
		return candidates;
	}
	
	private static final class TestCommandRegistry implements CommandRegistry {
		private final Map<String, Command> registry = new HashMap<String, Command>();

		public void registerCommand(String name, Completor completor) {
			registry.put(name, new TestCommand(name, completor));
		}

		@Override
		public String[] getCommandNames() {
			return registry.keySet().toArray(new String[0]);
		}

		@Override
		public Command getCommand(String name) {
			return registry.get(name);
		}
	}

	private static class TestCommand implements Command {
		
		private final String name;
		private final Completor completor;
		
		public TestCommand(String name, Completor completor) {
			this.name = name;
			this.completor = completor;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public Object execute(CommandContext context) {
			return null;
		}
		
		@Override
		public Completor getCompletor() {
			return completor;
		}
	}
	
}