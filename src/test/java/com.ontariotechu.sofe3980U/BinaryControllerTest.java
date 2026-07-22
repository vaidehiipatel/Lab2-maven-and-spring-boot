package com.ontariotechu.sofe3980U;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.junit.runner.RunWith;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.junit4.*;

import static org.hamcrest.Matchers.containsString;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BinaryController.class)
public class BinaryControllerTest {

    @Autowired
    private MockMvc mvc;

    // ---------- original three test cases ----------

    @Test
    public void getDefault() throws Exception {
        this.mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("calculator"))
            .andExpect(model().attribute("operand1", ""))
            .andExpect(model().attribute("operand1Focused", false));
    }

    @Test
    public void getParameter() throws Exception {
        this.mvc.perform(get("/").param("operand1","111"))
            .andExpect(status().isOk())
            .andExpect(view().name("calculator"))
            .andExpect(model().attribute("operand1", "111"))
            .andExpect(model().attribute("operand1Focused", true));
    }

    @Test
    public void postParameter() throws Exception {
        this.mvc.perform(post("/").param("operand1","111").param("operator","+").param("operand2","111"))
            .andExpect(status().isOk())
            .andExpect(view().name("result"))
            .andExpect(model().attribute("result", "1110"))
            .andExpect(model().attribute("operand1", "111"));
    }

    // ---------- three additional general test cases ----------

    /** An unsupported operator must fall through to the error view. */
    @Test
    public void postInvalidOperator() throws Exception {
        this.mvc.perform(post("/").param("operand1","111").param("operator","%").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attribute("operator", "%"));
    }

    /** A missing operator is also invalid and must reach the error view. */
    @Test
    public void postMissingOperator() throws Exception {
        this.mvc.perform(post("/").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"));
    }

    /** An empty operand must be treated as zero rather than crashing. */
    @Test
    public void postEmptyOperand() throws Exception {
        this.mvc.perform(post("/").param("operand1","").param("operator","+").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(view().name("result"))
            .andExpect(model().attribute("result", "1010"));
    }

    // ---------- multiplication ----------

    @Test
    public void postMultiply() throws Exception {
        this.mvc.perform(post("/").param("operand1","111").param("operator","*").param("operand2","111"))
            .andExpect(status().isOk())
            .andExpect(view().name("result"))
            .andExpect(model().attribute("result", "110001"));
    }

    @Test
    public void postMultiplyByZero() throws Exception {
        this.mvc.perform(post("/").param("operand1","1011").param("operator","*").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "0"));
    }

    @Test
    public void postMultiplyByOne() throws Exception {
        this.mvc.perform(post("/").param("operand1","1011").param("operator","*").param("operand2","1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "1011"));
    }

    @Test
    public void postMultiplyCarryHeavy() throws Exception {
        this.mvc.perform(post("/").param("operand1","1111").param("operator","*").param("operand2","1111"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "11100001"));
    }

    // ---------- bitwise AND ----------

    @Test
    public void postAnd() throws Exception {
        this.mvc.perform(post("/").param("operand1","1100").param("operator","&").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(view().name("result"))
            .andExpect(model().attribute("result", "1000"));
    }

    @Test
    public void postAndWithZero() throws Exception {
        this.mvc.perform(post("/").param("operand1","1011").param("operator","&").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "0"));
    }

    @Test
    public void postAndUnequalLengths() throws Exception {
        this.mvc.perform(post("/").param("operand1","1111").param("operator","&").param("operand2","10"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "10"));
    }

    // ---------- bitwise OR ----------

    @Test
    public void postOr() throws Exception {
        this.mvc.perform(post("/").param("operand1","1100").param("operator","|").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(view().name("result"))
            .andExpect(model().attribute("result", "1110"));
    }

    @Test
    public void postOrWithZero() throws Exception {
        this.mvc.perform(post("/").param("operand1","1011").param("operator","|").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "1011"));
    }

    @Test
    public void postOrUnequalLengths() throws Exception {
        this.mvc.perform(post("/").param("operand1","100").param("operator","|").param("operand2","11"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("result", "111"));
    }
}
