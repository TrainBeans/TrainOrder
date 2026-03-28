package org.trainbeans.trainorder.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.trainbeans.trainorder.model.TrainOrder;
import org.trainbeans.trainorder.service.TrainOrderService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class TrainOrderController {

    private final TrainOrderService service;

    public TrainOrderController(TrainOrderService service) {
        this.service = service;
    }

    // ── List ───────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", service.getAllOrders());
        return "orders/list";
    }

    // ── Create ─────────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("order", service.newOrder());
        model.addAttribute("formAction", "/orders");
        model.addAttribute("pageTitle", "New Train Order");
        return "orders/form";
    }

    @PostMapping
    public String create(@ModelAttribute TrainOrder order,
                         @RequestParam(value = "toLines", required = false) List<String> toLines) {
        if (toLines != null) {
            order.setToLine(toLines.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining("\n")));
        }
        service.saveOrder(order);
        return "redirect:/orders";
    }

    // ── Print ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/print")
    public String print(@PathVariable Long id, Model model) {
        TrainOrder order = service.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        model.addAttribute("order", order);
        List<String> toLinesList = (order.getToLine() != null && !order.getToLine().isBlank())
                ? Arrays.asList(order.getToLine().split("\n", -1))
                : List.of("");
        model.addAttribute("toLinesList", toLinesList);
        return "orders/print";
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.deleteOrder(id);
        return "redirect:/orders";
    }
}
