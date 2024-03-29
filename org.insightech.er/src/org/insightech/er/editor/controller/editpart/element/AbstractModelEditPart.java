package org.insightech.er.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(AbstractModelEditPart.class
			.getName());

	private static final boolean DEBUG = false;
	
	@Override
	public void activate() {
		super.activate();

		AbstractModel model = (AbstractModel) this.getModel();
		model.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		AbstractModel model = (AbstractModel) this.getModel();
		model.removePropertyChangeListener(this);

		super.deactivate();
	}

	protected final ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected final Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	protected final void execute(final Command command) {
		this.getViewer().getEditDomain().getCommandStack().execute(command);
	}

	public final void propertyChange(PropertyChangeEvent event) {
		try {
			if (DEBUG) {
				logger.log(Level.INFO, this.getClass().getName() + ":"
						+ event.getPropertyName() + ":" + event.toString());
			}
			
			this.doPropertyChange(event);

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	protected void doPropertyChange(PropertyChangeEvent event) {
	}

}
