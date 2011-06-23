package com.intellij.tapestry.intellij.toolwindow;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.tapestry.core.java.IJavaClassType;
import com.intellij.tapestry.core.java.IJavaField;
import com.intellij.tapestry.core.model.presentation.InjectedElement;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.core.util.TapestryIcons;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.core.java.IntellijJavaField;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import com.intellij.tapestry.intellij.toolwindow.nodes.*;
import com.intellij.ui.treeStructure.actions.CollapseAllAction;
import com.intellij.ui.treeStructure.actions.ExpandAllAction;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DependenciesTab {

    private JPanel _mainPanel;
    private JTree _dependenciesTree;
    private JSplitPane _splitPane;
    private JTextPane _documentationPane;
    private JToolBar _toolbar;
    private NavigateToElementAction _navigateToElementAction;
    private NavigateToUsageAction _navigateToUsageAction;

    public DependenciesTab() {
        _splitPane.setDividerLocation(0.5);

        UIUtil.setLineStyleAngled(_dependenciesTree);
        _dependenciesTree.setCellRenderer(new DependenciesTreeCellRenderer());

        _navigateToElementAction = new NavigateToElementAction();
        _navigateToUsageAction = new NavigateToUsageAction();

        _dependenciesTree.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent event) {
                        TreePath selected = _dependenciesTree.getSelectionPath();

                        // When is double click
                        if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1 && selected != null) {
                            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) _dependenciesTree.getSelectionPath().getLastPathComponent();
                            Object selectedObject = selectedNode.getUserObject();

                            if (selectedNode.getParent() instanceof InjectedPagesNode || selectedNode.getParent() instanceof EmbeddedComponentsNode) {
                              if (selectedObject instanceof InjectedElement) {
                                ((IntellijJavaField) ((InjectedElement) selectedObject).getField()).getPsiField().navigate(true);
                              }
                            }
                        }

                        // When is right click and a object is selected
                        if (event.getButton() == MouseEvent.BUTTON3) {

                            // When object it's selected
                            if (selected != null) {
                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) _dependenciesTree.getSelectionPath().getLastPathComponent();
                                Object selectedObject = selectedNode.getUserObject();

                                if (selectedObject instanceof InjectedElement || selectedObject instanceof PresentationLibraryElement || selectedObject instanceof IResource) {
                                    DefaultActionGroup actions = new DefaultActionGroup("NavigateToGroup", true);

                                    actions.add(_navigateToElementAction);
                                    actions.add(_navigateToUsageAction);

                                    actions.addSeparator();

                                    actions.add(new CollapseAllAction(_dependenciesTree));
                                    actions.add(new ExpandAllAction(_dependenciesTree));

                                    ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("ElementUsagesTree", actions);
                                    popupMenu.getComponent().show(event.getComponent(), event.getX(), event.getY());
                                    event.consume();
                                }
                            }

                            // When object it's not selected
                            if (selected == null) {
                                DefaultActionGroup actions = new DefaultActionGroup("NavigateToGroup", true);

                                actions.add(new CollapseAllAction(_dependenciesTree));
                                actions.add(new ExpandAllAction(_dependenciesTree));

                                ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("ElementUsagesTree", actions);
                                popupMenu.getComponent().show(event.getComponent(), event.getX(), event.getY());
                                event.consume();
                            }
                        }
                    }
                }
        );
        _dependenciesTree.addTreeSelectionListener(
                new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent event) {

                        if (event.getNewLeadSelectionPath() != null) {
                            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();
                            Object selectedObject = selectedNode.getUserObject();

                            Object selectedClass = ((DefaultMutableTreeNode) event.getNewLeadSelectionPath().getPath()[0]).getUserObject();
                            IJavaClassType elementClass = ((IJavaClassType) ((PresentationLibraryElement) selectedClass).getElementClass());

                            String text = null;
                            try {
                                /*if (selectedNode instanceof EmbeddedComponentsNode) {
                                    text = SerializationHandler.getInstance().serializeToDependencies(((EmbeddedComponentsNode) selectedNode).getEmbeddedComponentNodes(), "Embedded");
                                } else if (selectedNode instanceof InjectedPagesNode) {
                                    text = SerializationHandler.getInstance().serializeToDependencies(((InjectedPagesNode) selectedNode).getInjectedComponentNodes(), "Injected");
                                } else if (selectedNode instanceof EmbeddedComponentNode) {
                                    text = SerializationHandler.getInstance().serializeToDocumentation(((EmbeddedComponentNode) selectedNode).getInjectedComponent(), elementClass);
                                } else if (selectedNode instanceof InjectedPageNode) {
                                    text = SerializationHandler.getInstance().serializeToDocumentation(((InjectedPageNode) selectedNode).getInjectedPage(), elementClass);
                                } else if (selectedNode instanceof EmbeddedTemplateNode) {
                                    text = SerializationHandler.getInstance().serializeToDependencies(((EmbeddedTemplateNode) selectedNode).getEmbeddedTemplateNodes(), selectedNode.toString());
                                } else {
                                    text = SerializationHandler.getInstance().serializeToDocumentation(selectedObject, null);
                                }*/
                            } catch (RuntimeException e) {
                                text = null;
                            }

                            if (_dependenciesTree.getSelectionCount() == 1 && text != null && !(selectedNode instanceof EmbeddedComponentsNode) && !(selectedNode instanceof InjectedPagesNode)) {
                                _documentationPane.setText(text);
                                _documentationPane.setSelectionStart(0);
                                _documentationPane.setSelectionEnd(0);

                                _navigateToElementAction.getTemplatePresentation().setEnabled(true);

                                if (selectedNode instanceof EmbeddedTemplateNode || selectedNode.isRoot())
                                    _navigateToUsageAction.getTemplatePresentation().setEnabled(false);
                                else
                                    _navigateToUsageAction.getTemplatePresentation().setEnabled(true);

                            } else {
                                _documentationPane.setText(text);

                                _navigateToElementAction.getTemplatePresentation().setEnabled(false);
                                _navigateToUsageAction.getTemplatePresentation().setEnabled(false);
                            }
                        }
                    }
                }
        );
        _dependenciesTree.setVisible(false);

        _navigateToElementAction.getTemplatePresentation().setEnabled(false);
        _navigateToUsageAction.getTemplatePresentation().setEnabled(false);

        CollapseAllAction collapseAllAction = new CollapseAllAction(_dependenciesTree);
        ExpandAllAction expandAllAction = new ExpandAllAction(_dependenciesTree);

        ActionButton navigateToElement = new ActionButton(_navigateToElementAction, _navigateToElementAction.getTemplatePresentation(), "Navigate to Element", new Dimension(24, 24));
        navigateToElement.setToolTipText("Navigate to Element");
        _toolbar.add(navigateToElement);

        ActionButton navigateToUsage = new ActionButton(_navigateToUsageAction, _navigateToUsageAction.getTemplatePresentation(), "Navigate to Usage", new Dimension(24, 24));
        navigateToUsage.setToolTipText("Navigate to Usage");
        _toolbar.add(navigateToUsage);
        _toolbar.addSeparator();

        ActionButton expandAll = new ActionButton(expandAllAction, expandAllAction.getTemplatePresentation(), expandAllAction.getTemplatePresentation().getText(), new Dimension(24, 24));
        expandAll.setToolTipText("Expand All");
        _toolbar.add(expandAll);

        ActionButton collapseAll = new ActionButton(collapseAllAction, collapseAllAction.getTemplatePresentation(), collapseAllAction.getTemplatePresentation().getText(), new Dimension(24, 24));
        collapseAll.setToolTipText("Collapse All");
        _toolbar.add(collapseAll);

    }

    public JPanel getMainPanel() {
        return _mainPanel;
    }

    /**
     * Shows the dependencies of an element.
     *
     * @param module  the module the element belongs to.
     * @param element the element to show the dependencies of.
     */
    public void showDependencies(Module module, Object element) {
        String text = null;
        try {
            //text = SerializationHandler.getInstance().serializeToDocumentation(element, null);
        } catch (RuntimeException e) {
            text = null;
        }

        if (shouldShowDependencies(element)) {
            _dependenciesTree.setVisible(true);

            _dependenciesTree.setModel(null);
            _dependenciesTree.setModel(new DefaultTreeModel(new DependenciesRootNode(element)));

            _documentationPane.setText(text);

            _navigateToElementAction.getTemplatePresentation().setEnabled(false);
            _navigateToUsageAction.getTemplatePresentation().setEnabled(false);
        } else {
            clear();
        }
    }

    /**
     * Clear the documentation window.
     */
    public void clear() {
        _dependenciesTree.setVisible(false);
        _documentationPane.setText("<html><head></head><body></body></html>");
    }

    private boolean shouldShowDependencies(Object element) {
        return element instanceof PresentationLibraryElement;
    }

    public class NavigateToElementAction extends AnAction {

        public NavigateToElementAction() {
            super("Navigate to Element", "Navigate to the selected element class", TapestryIcons.NAVIGATE);
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(AnActionEvent event) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) _dependenciesTree.getSelectionPath().getLastPathComponent();
            Object selectedObject = selectedNode.getUserObject();

            if (selectedObject instanceof PresentationLibraryElement) {
                PsiClass psiClass = ((IntellijJavaClassType) ((PresentationLibraryElement) selectedObject).getElementClass()).getPsiClass();

                if (psiClass != null) {
                    psiClass.navigate(true);
                }
            }
            if (selectedObject instanceof InjectedElement) {
                PsiClass psiClass = ((IntellijJavaClassType) ((InjectedElement) selectedObject).getElement().getElementClass()).getPsiClass();

                if (psiClass != null) {
                    psiClass.navigate(true);
                }
            }
            if (selectedObject instanceof IntellijResource) {
                PsiFile file = ((IntellijResource) selectedObject).getPsiFile();

                if (file != null) {
                    file.navigate(true);
                }
            }
        }
    }

    private class NavigateToUsageAction extends AnAction {

        public NavigateToUsageAction() {
            super("Navigate to Usage", "Navigate to part of code where the selected element is used", TapestryIcons.REFERENCE);
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(AnActionEvent event) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) _dependenciesTree.getSelectionPath().getLastPathComponent();
            Object selectedObject = selectedNode.getUserObject();

            if (selectedObject instanceof PresentationLibraryElement || selectedObject instanceof InjectedElement) {
                PsiField field = null;
                PsiFile file = null;

                // Embedded component
                if (selectedNode instanceof EmbeddedComponentNode) {
                    IJavaField elementField = ((EmbeddedComponentNode) selectedNode).getInjectedComponent().getField();

                    if (elementField != null)
                        field = ((IntellijJavaField) elementField).getPsiField();
                    else
                        file = ((IntellijResource) ((EmbeddedTemplateNode) selectedNode.getParent()).getUserObject()).getPsiFile();
                }

                // Injected page
                if (selectedNode instanceof InjectedPageNode) {
                    IJavaField elementField = ((InjectedPageNode) selectedNode).getInjectedPage().getField();

                    if (elementField != null)
                        field = ((IntellijJavaField) elementField).getPsiField();
                    else
                        file = ((IntellijResource) ((EmbeddedTemplateNode) selectedNode.getParent()).getUserObject()).getPsiFile();
                }

                if (field != null) {
                    field.navigate(true);
                }

                if (file != null) {
                    file.navigate(true);
                }
            }
        }
    }
}