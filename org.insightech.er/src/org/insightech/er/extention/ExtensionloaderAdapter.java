package org.insightech.er.extention;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.insightech.er.Activator;
import org.insightech.er.editor.ERDiagramEditor;

/**
 * ExtensionLoaderとERDaiagramEditorのアダプタ
 */
public class ExtensionLoaderAdapter {

	private ExtensionLoader loader;

	public ExtensionLoaderAdapter() {
		this.loader = new ExtensionLoader();
		
		try {
			loader.loadExtensions();

		} catch (CoreException e) {
			Activator.showExceptionDialog(e);
		}
	}

	/**
	 * ExtensionLoaderから読み込んだクラスをERDiagramEditorのActionRegisterに追加する
	 * 
	 * @param editor
	 *            追加されるERDiagramEditor
	 * @param registry
	 *            ActionRegister
	 * @param selectionActionList
	 *            追加されるActionのIDリスト
	 */
	public void addActions(ERDiagramEditor editor, ActionRegistry registry,
			List<String> selectionActionList) {
		Map objMap = loader.getObjMap();
		List nameList = loader.getNameList();

		for (int i = 0; i < nameList.size(); i++) {
			IExtendAction exaction = (IExtendAction) objMap
					.get(nameList.get(i));

			IAction action = exaction.createIAction(editor, (String) nameList
					.get(i));
			selectionActionList.add(action.getId());
			registry.registerAction(action);
		}
	}

	/**
	 * MenuManagerにExtentionLoaderから読み込んだクラスを追加
	 * 
	 * @param menuMgr
	 *            追加されるポップアップメニューのマネジャー
	 * @param actionregistry
	 *            ポップアップメニューに追加するアクションを読み込む
	 */
	public void addERDiagramPopupMenu(MenuManager menuMgr,
			ActionRegistry actionregistry) {
		List nameList = loader.getNameList();
		Map pathMap = loader.getPathMap();
		Map objMap = loader.getObjMap();
		for (int i = 0; i < nameList.size(); i++) {
			try {
				IAction action = actionregistry
						.getAction(((IExtendAction) objMap.get(nameList.get(i)))
								.getId());
				menuMgr
						.findMenuUsingPath(
								(String) pathMap.get(nameList.get(i))).add(
								action);
			} catch (NullPointerException e) {
				IAction action = actionregistry
						.getAction(((IExtendAction) objMap.get(nameList.get(i)))
								.getId());
				menuMgr.add(action);
			}
		}
	}

	public ExtensionLoader getLoader() {
		return loader;
	}

}
