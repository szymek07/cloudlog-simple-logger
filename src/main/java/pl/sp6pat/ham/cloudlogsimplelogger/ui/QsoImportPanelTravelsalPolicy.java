package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import java.awt.*;
import java.util.List;

public class QsoImportPanelTravelsalPolicy extends FocusTraversalPolicy {
    private final List<Component> order;

    public QsoImportPanelTravelsalPolicy(List<Component> order) {
        this.order = order;
    }

    @Override
    public Component getComponentAfter(Container container, Component component) {
        int idx = order.indexOf(component);
        if (idx == -1) return order.get(0); // Domyślnie przechodzimy do pierwszego komponentu
        return order.get((idx + 1) % order.size());
    }

    @Override
    public Component getComponentBefore(Container container, Component component) {
        int idx = order.indexOf(component);
        if (idx == -1) return order.get(order.size() - 1); // Domyślnie przechodzimy do ostatniego komponentu
        return order.get((idx - 1 + order.size()) % order.size());
    }

    @Override
    public Component getFirstComponent(Container container) {
        return order.get(0);
    }

    @Override
    public Component getLastComponent(Container container) {
        return order.get(order.size() - 1);
    }

    @Override
    public Component getDefaultComponent(Container container) {
        return order.get(0);
    }

}
