package org.trainbeans.trainorder.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.trainbeans.trainorder.model.TrainOrder;
import org.trainbeans.trainorder.service.TrainOrderService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({HomeController.class, TrainOrderController.class})
class HomeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TrainOrderService service;

    @Test
    void rootRedirectsToOrders() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));
    }

    @Test
    void ordersPageReturnsListView() throws Exception {
        when(service.getAllOrders()).thenReturn(List.of(
                TrainOrder.builder().id(1L).orderNumber("1").fromLocation("Northtown").build()
        ));

        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders"));
    }
}
