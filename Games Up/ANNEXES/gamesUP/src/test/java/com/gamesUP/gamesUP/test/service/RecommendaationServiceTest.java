package com.gamesUP.gamesUP.test.service;

import com.gamesUP.gamesUP.dto.RecommendationDTO;
import com.gamesUP.gamesUP.dto.UserDataDTO;
import com.gamesUP.gamesUP.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private RecommendationService service;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        service = new RecommendationService();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "pythonUrl", "http://test/recommendations/");
    }

    private Object buildInternalResponse(List<RecommendationDTO> recs) throws Exception {
        Class<?> respClass = Class.forName("com.gamesUP.gamesUP.service.RecommendationService$RecommendationsResponse");
        Constructor<?> ctor = respClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object respInstance = ctor.newInstance();
        Method setter = respClass.getDeclaredMethod("setRecommendations", List.class);
        setter.setAccessible(true);
        setter.invoke(respInstance, recs);
        return respInstance;
    }

    @Test
    void shouldReturnList_whenResponseIs2xxAndBodyNotNull() throws Exception {
        RecommendationDTO r1 = RecommendationDTO.builder().game_id(1).rating(4.5).build();
        RecommendationDTO r2 = RecommendationDTO.builder().game_id(2).rating(3.0).build();
        Object body = buildInternalResponse(List.of(r1, r2));
        ResponseEntity<Object> resp = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), eq(Class.forName("com.gamesUP.gamesUP.service.RecommendationService$RecommendationsResponse"))))
                .thenReturn((ResponseEntity) resp);

        UserDataDTO userData = new UserDataDTO();
        List<RecommendationDTO> result = service.getRecommendations(userData);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getGame_id());
        assertEquals(4.5, result.get(0).getRating());
        verify(restTemplate).postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void shouldReturnEmptyList_whenResponseBodyContainsEmptyList() throws Exception {
        Object body = buildInternalResponse(List.of());
        ResponseEntity<Object> resp = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), eq(Class.forName("com.gamesUP.gamesUP.service.RecommendationService$RecommendationsResponse"))))
                .thenReturn((ResponseEntity) resp);

        List<RecommendationDTO> result = service.getRecommendations(new UserDataDTO());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate).postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void shouldThrowRuntime_whenResponseIs2xxButBodyIsNull() {
        ResponseEntity<Object> resp = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), any(Class.class)))
                .thenReturn((ResponseEntity) resp);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getRecommendations(new UserDataDTO()));
        assertTrue(ex.getMessage().toLowerCase().contains("erreur") || ex.getMessage().toLowerCase().contains("http"));
    }

    @Test
    void shouldThrowRuntime_whenResponseIsNot2xx() throws Exception {
        Object body = buildInternalResponse(List.of(RecommendationDTO.builder().game_id(1).rating(1.0).build()));
        ResponseEntity<Object> resp = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), eq(Class.forName("com.gamesUP.gamesUP.service.RecommendationService$RecommendationsResponse"))))
                .thenReturn((ResponseEntity) resp);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getRecommendations(new UserDataDTO()));
        assertTrue(ex.getMessage().contains("INTERNAL_SERVER_ERROR"));
    }

    @Test
    void shouldPropagate_whenRestTemplateThrows() {
        when(restTemplate.postForEntity(eq("http://test/recommendations/"), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new RestClientException("connection fail"));

        RestClientException ex = assertThrows(RestClientException.class, () -> service.getRecommendations(new UserDataDTO()));
        assertEquals("connection fail", ex.getMessage());
    }
}
