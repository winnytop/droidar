package worldData;

import gl.MeshComponent;
import gl.ObjectPicker;

import javax.microedition.khronos.opengles.GL10;

import util.EfficientList;

import commands.Command;
import components.Component;

public class Obj extends AbstractObj {

	private static final String LOG_TAG = "Obj";
	EfficientList<Component> myComponents = new EfficientList<Component>();

	public void setMyComponents(EfficientList<Component> myComponents) {
		this.myComponents = myComponents;
	}

	MeshComponent myGraphicsComponent;

	public MeshComponent getGraphicsComponent() {
		return myGraphicsComponent;
	}

	// public void updateComponents(Component component) {
	// Log.e("Obj.update()", "update not catched from: " + component);
	// }

	/**
	 * is called from time to time by the {@link World} Thread
	 * 
	 * @param timeDelta
	 *            how many ms have passed since last update
	 */
	public boolean update(float timeDelta) {
		final int lenght = myComponents.myLength;
		for (int i = 0; i < lenght; i++) {
			if (myComponents.get(i) != null)
				myComponents.get(i).update(timeDelta, this);
		}
		return true;
	}

	/**
	 * @param uniqueCompName
	 *            look into {@link Consts} and there the COMP_.. strings for
	 *            component types
	 * @param comp
	 */
	public void setComp(Component comp) {
		// TODO rename to add.. and return boolean if could be added
		// TODO put the String info in the comp itself or remove it, its crap
		if (comp instanceof MeshComponent) {
			setMyGraphicsComponent((MeshComponent) comp);
		}
		if (comp != null && myComponents.contains(comp) == -1)
			myComponents.add(comp);
	}

	public void setMyGraphicsComponent(MeshComponent myGraphicsComponent) {
		this.myGraphicsComponent = myGraphicsComponent;
		myGraphicsComponent.setMyParentObj(this);
	}

	public EfficientList<Component> getMyComponents() {
		return myComponents;
	}

	public void draw(GL10 gl) {
		// final Component myGraphicsComponent = myComponents
		// .get(Consts.COMP_GRAPHICS);
		if (myGraphicsComponent == null)
			return;

		/*
		 * nessecary for objects with picking disabled (wich cant be clicked).
		 * this makes sure this objects will be drawn in black so no color key
		 * in the @GlObjectPicker map will match this object
		 * 
		 * its important to do this here and not in the MeshComponent itself,
		 * because if you set a selectionColor to a meshGroup and then clear the
		 * color if a Mesh has no selectionColor all the children of the
		 * meshGroup wont have the correct selection color!
		 */
		if (ObjectPicker.readyToDrawWithColor) {
			gl.glColor4f(0, 0, 0, 1);
		} else {
			/*
			 * before drawing a new object, reset the color to white TODO better
			 * way? gl_clear(color_bit) or anything like this?
			 */
			gl.glColor4f(1, 1, 1, 1);
		}

		myGraphicsComponent.setMatrixAndDraw(gl);
	}

	@Override
	public void setOnClickCommand(Command c) {
		super.setOnClickCommand(c);
		MeshComponent m = getComp(MeshComponent.class);
		if (m != null) {
			m.enableMeshPicking(this);
		}
	}

	public boolean remove(Component compToRemove) {
		return myComponents.remove(compToRemove);
	}

	// public boolean accept(Visitor visitor) {
	// return visitor.default_visit(this);
	// }

	/**
	 * @param componentSubclass
	 * @return true if any of the {@link Obj} {@link Component}s is a of the
	 *         specified class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean hasComponent(Class componentSubclass) {
		if (getComp(componentSubclass) != null)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T getComp(Class<T> componentSubclass) {

		if (componentSubclass.isAssignableFrom(MeshComponent.class)) {
			// Log.e(LOG_TAG, "Fast access to obj.meshcomp=" +
			// myGraphicsComponent);
			return (T) myGraphicsComponent;
		}

		for (int i = 0; i < myComponents.myLength; i++) {
			Component a = myComponents.get(i);
			if (componentSubclass.isAssignableFrom(a.getClass()))
				return (T) a;
		}
		return null;
	}

	// public String getDebugInfos() {
	// return myGraphicsComponent.toString();
	// }

	// public Component getComponent(String compName) {
	// return myComponents.get(compName);
	// }

	// @Override
	// public void setLongDescr(String info) {
	// getMyInfoObject().setLongDescr(info);
	// }

	// @Override
	// public void setShortDescr(String name) {
	// myInfoObj.setShortDescr(name);
	// }

}
