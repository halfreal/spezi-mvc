package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractControllerTest {

	private static class SimpleController extends
			AbstractController<SimpleModel> {

		protected SimpleController(SimpleModel model) {
			super(model);
		}

	}

	private static class SimpleModel extends AbstractModel {

	}

	@Test
	public void testConstructor() {
		SimpleModel model = new SimpleModel();
		SimpleController controller = new SimpleController(model);

		assertNotNull(controller.getModel());
		assertEquals(model, controller.getModel());
	}

}
