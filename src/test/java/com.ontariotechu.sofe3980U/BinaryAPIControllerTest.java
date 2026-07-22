package com.ontariotechu.sofe3980U;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
@WebMvcTest(BinaryAPIController.class)
public class BinaryAPIControllerTest {

    @Autowired
    private MockMvc mvc;

    // ---------- original two test cases ----------

    @Test
    public void add() throws Exception {
        this.mvc.perform(get("/add").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(content().string("10001"));
    }

    @Test
    public void add2() throws Exception {
        this.mvc.perform(get("/add_json").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.operand1").value(111))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operand2").value(1010))
            .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(10001))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operator").value("add"));
    }

    // ---------- three additional general test cases ----------

    /** Missing query parameters must default to zero, not throw. */
    @Test
    public void addMissingParameters() throws Exception {
        this.mvc.perform(get("/add"))
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    /** Operands of different lengths must align correctly and carry across. */
    @Test
    public void addUnequalLengths() throws Exception {
        this.mvc.perform(get("/add").param("operand1","1").param("operand2","11111"))
            .andExpect(status().isOk())
            .andExpect(content().string("100000"));
    }

    /** Leading zeros must be stripped from the JSON echo of the operands. */
    @Test
    public void addJsonStripsLeadingZeros() throws Exception {
        this.mvc.perform(get("/add_json").param("operand1","000111").param("operand2","0001010"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.operand1").value(111))
            .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(10001));
    }

    // ---------- multiplication ----------

    @Test
    public void multiply() throws Exception {
        this.mvc.perform(get("/multiply").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(content().string("1000110"));
    }

    @Test
    public void multiplyByZero() throws Exception {
        this.mvc.perform(get("/multiply").param("operand1","1011").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    public void multiplyByOne() throws Exception {
        this.mvc.perform(get("/multiply").param("operand1","1011").param("operand2","1"))
            .andExpect(status().isOk())
            .andExpect(content().string("1011"));
    }

    @Test
    public void multiplyCarryHeavy() throws Exception {
        this.mvc.perform(get("/multiply").param("operand1","1111").param("operand2","1111"))
            .andExpect(status().isOk())
            .andExpect(content().string("11100001"));
    }

    @Test
    public void multiplyJson() throws Exception {
        this.mvc.perform(get("/multiply_json").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.operand1").value(111))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operand2").value(1010))
            .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1000110))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operator").value("multiply"));
    }

    // ---------- bitwise AND ----------

    @Test
    public void and() throws Exception {
        this.mvc.perform(get("/and").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(content().string("10"));
    }

    @Test
    public void andWithZero() throws Exception {
        this.mvc.perform(get("/and").param("operand1","1011").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    public void andUnequalLengths() throws Exception {
        this.mvc.perform(get("/and").param("operand1","1111").param("operand2","10"))
            .andExpect(status().isOk())
            .andExpect(content().string("10"));
    }

    @Test
    public void andJson() throws Exception {
        this.mvc.perform(get("/and_json").param("operand1","1100").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1000))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operator").value("and"));
    }

    // ---------- bitwise OR ----------

    @Test
    public void or() throws Exception {
        this.mvc.perform(get("/or").param("operand1","111").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(content().string("1111"));
    }

    @Test
    public void orWithZero() throws Exception {
        this.mvc.perform(get("/or").param("operand1","1011").param("operand2","0"))
            .andExpect(status().isOk())
            .andExpect(content().string("1011"));
    }

    @Test
    public void orUnequalLengths() throws Exception {
        this.mvc.perform(get("/or").param("operand1","100").param("operand2","11"))
            .andExpect(status().isOk())
            .andExpect(content().string("111"));
    }

    @Test
    public void orJson() throws Exception {
        this.mvc.perform(get("/or_json").param("operand1","1100").param("operand2","1010"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1110))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operator").value("or"));
    }
}
