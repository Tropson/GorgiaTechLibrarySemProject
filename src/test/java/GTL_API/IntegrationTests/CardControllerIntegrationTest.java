package GTL_API.IntegrationTests;

import GTL_API.MainApplicationClass;
import GTL_API.Models.Entities.CardEntity;
import GTL_API.Models.ReturnModels.CardReturn;
import GTL_API.Repositories.CardRepository.ICardRepository;
import GTL_API.TestDataSourceConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {MainApplicationClass.class, TestDataSourceConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CardControllerIntegrationTest {
    private String token;

    @Autowired
    private MockMvc mvc;



    @Autowired
    private ICardRepository cardRepository;

    @Before
    public void login() throws Exception{
        MvcResult result = mvc.perform(post("/gtl/auth/login")
                .content("{\"login\": \"loginMaster\", \"password\": \"password\"}")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        token = result.getResponse().getContentAsString();
    }

    @Test
    public void findCardByNumberControllerMethodShouldPass() {
        try {
            mvc.perform(get("/gtl/card/1025435856")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isFound())
                    .andExpect(content().json("{\n" +
                            "    \"number\": 1025435856,\n" +
                            "    \"expirationDate\": \"2020-08-28\",\n" +
                            "    \"picture\": \"9DbC0FFD-aeFF-5F42-862B-cC15B7Ce79Bb\\r\\n\",\n" +
                            "    \"libraryEmployeeId\": 25\n" +
                            "}"));
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    public void findCardThatDoesNotExistsShouldGiveBackErrorMessage() {
        try {
            mvc.perform(get("/gtl/card/-1")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Card with number -1 was not found."));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void createCardShouldCreateIt(){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ResultActions resultActions = mvc.perform(post("/gtl/card/")
                    .header("Authorization", "Bearer " + token)
                    .content("{\n" +
                    "\t\"expirationDate\":\"2098-08-08\",\n" +
                    "\t\"libraryEmployeeId\": 25,\n" +
                    "\t\"picture\":\"Test\"\n" +
                    "}").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            MvcResult mvcResult = resultActions.andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            CardReturn returned = objectMapper.readValue(contentAsString, CardReturn.class);
            Assert.assertEquals("Test", returned.getPicture());
            cardRepository.deleteById(returned.getNumber());
        }catch (Exception e){
            Assert.fail();
        }
    }

    /**
     * The test method runs a request of deleting card. It assets expected content of status and content. Afterwards,
     * by using cardRepository instance the deleted card's column value is changed back to not deleted.
     */
    @Test
    public void deleteCardShouldChangeItsColumnValueToTrue(){
        try{
            mvc.perform(delete("/gtl/card/1025435856")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Card with number: 1025435856 was successfully deleted."));
            Optional<CardEntity> foundOptional = cardRepository.findById(1025435856);
            if(foundOptional.isPresent()){
                CardEntity found = foundOptional.get();
                found.setDeleted(false);
                cardRepository.save(found);
            }else{
                Assert.fail();
            }
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void deleteCardThatDoesNotReturnNotFoundResponse(){
        try{
            mvc.perform(delete("/gtl/card/25")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Card with number 25 was not found."));
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testCreatingCardWithoutEmployeeIdProvided_ShouldReturnAppropriateMessage(){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ResultActions resultActions = mvc.perform(post("/gtl/card/")
                    .header("Authorization", "Bearer " + token)
                    .content("{\n" +
                    "\t\"expirationDate\":\"2098-08-08\",\n" +
                    "\t\"picture\":\"Test\"\n" +
                    "}").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content()
                            .string("Identification of person who makes the card must be provided." + "\n"));

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testCreatingCardWithoutExpirationDateProvided_ShouldReturnAppropriateMessage(){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ResultActions resultActions = mvc.perform(post("/gtl/card/")
                    .header("Authorization", "Bearer " + token)
                    .content("{\n" +
                    "\t\"libraryEmployeeId\": 25,\n" +
                    "\t\"picture\":\"Test\"\n" +
                    "}").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content()
                            .string("Expiration date must be provided." + "\n"));

        }catch (Exception e){
            Assert.fail();
        }
    }
}
