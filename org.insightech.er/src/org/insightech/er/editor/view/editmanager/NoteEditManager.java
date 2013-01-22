package org.insightech.er.editor.view.editmanager;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class NoteEditManager extends DirectEditManager {

	private Note note;
	private Font font;

	public NoteEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator) {
		super(source, editorType, locator);
		this.note = (Note) source.getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initCellEditor() {
		final TextCellEditor editor = (TextCellEditor) this.getCellEditor();

		if (note.getText() != null) {
			editor.setValue(note.getText());
		}

		final Text text = (Text) editor.getControl();

		if (this.font != null) {
			text.setFont(this.font);
		}

		text.selectAll();
	}

	public void setFont(final Font f) {
		this.font = f;
	}
}
