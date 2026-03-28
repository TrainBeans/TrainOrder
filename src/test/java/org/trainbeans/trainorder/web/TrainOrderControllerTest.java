package org.trainbeans.trainorder.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.trainbeans.trainorder.model.TrainOrder;
import org.trainbeans.trainorder.service.TrainOrderService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainOrderController.class)
class TrainOrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TrainOrderService service;

    @Test
    void listReturns200WithOrdersAttribute() throws Exception {
        when(service.getAllOrders()).thenReturn(List.of(
                TrainOrder.builder().id(1L).orderNumber("1").fromLocation("Northtown").build()
        ));

        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void newFormReturns200() throws Exception {
        when(service.newOrder()).thenReturn(new TrainOrder());

        mvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/form"))
                .andExpect(model().attributeExists("order", "formAction", "pageTitle"));
    }

    @Test
    void createRedirectsToList() throws Exception {
        when(service.saveOrder(any())).thenReturn(TrainOrder.builder().id(1L).build());

        mvc.perform(post("/orders")
                        .param("orderNumber", "5")
                        .param("fromLocation", "Depot"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));
    }

    @Test
    void printViewReturns200() throws Exception {
        when(service.getOrderById(1L)).thenReturn(java.util.Optional.of(
                TrainOrder.builder().id(1L).orderNumber("5").build()
        ));

        mvc.perform(get("/orders/1/print"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/print"))
                .andExpect(model().attributeExists("order", "toLinesList"));
    }

    @Test
    void deleteRedirectsToList() throws Exception {
        mvc.perform(post("/orders/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(service).deleteOrder(anyLong());
    }
}
