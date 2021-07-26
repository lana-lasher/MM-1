package com.LanaApp.MoveMedical;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MovieStudioDto;
import dto.MoviesDto;
import implementation.ReadImplementation;
import implementation.SaveImplementation;
import implementation.StudioExists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MoveMedicalApplicationTests {

	@Mock
	ReadImplementation readService;
	@Mock
	SaveImplementation saveService;
	@Mock
	StudioExists studioExists;

	@InjectMocks
	Controller controller;

	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		 mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		objectMapper = new ObjectMapper();
	}


	@Test
    public void testGetMovieInfo() throws Exception {
	List<MoviesDto> moviesDtoList = new ArrayList<>();

	MoviesDto moviesDto = MoviesDto.builder()
			.movieId(1L)
			.movieName("Movie1")
			.releaseDate("2021-07-22")
			.build();

		MoviesDto moviesDto2 = MoviesDto.builder()
				.movieId(2L)
				.movieName("Movie2")
				.releaseDate("2022-07-22")
				.build();

		moviesDtoList.add(moviesDto);
		moviesDtoList.add(moviesDto2);

	    when(readService.getMovieData(1L,false)).thenReturn(moviesDtoList);

	    List<MoviesDto> resultList = readService.getMovieData(1L,false);

		assertNotNull(resultList);
		assertSame(resultList,moviesDtoList);
		verify(readService, times(1)).getMovieData(Mockito.anyLong(),Mockito.anyBoolean());
}

   @Test
   public void testAddStudio() throws Exception {
		List<MovieStudioDto> studioList = new ArrayList<>();
	   MovieStudioDto studioDto = MovieStudioDto.builder()
			   .studioName("s1")
			   .build();
	   studioList.add(studioDto);

	   ResponseEntity<String> testResponse = new ResponseEntity<String> ("successfully inserted studio",HttpStatus.OK);

	   when(saveService.addStudios(studioList)).thenReturn(testResponse);
	   ResponseEntity<String> result = saveService.addStudios(studioList);
	   assertEquals(result,testResponse);
	   verify(saveService, times(1)).addStudios(Mockito.anyList());

   }

	@Test
	public void testAddStudioBlankStudio() throws Exception {
		List<MovieStudioDto> studioList = new ArrayList<>();
		MovieStudioDto studioDto = MovieStudioDto.builder()
				.studioName("")
				.build();
		studioList.add(studioDto);

		MvcResult result = mockMvc.perform(post("/LanaMoveMedicalApp/addStudios",studioList)).andExpect(status().isBadRequest()).andReturn();
	}

   @Test
   public void testDoesStudioExist() throws Exception {
		String studioName = "Studio 1";
		boolean exists = true;
		when(studioExists.exists(studioName)).thenReturn(exists);
		boolean result = studioExists.exists(studioName);
		assertTrue(result);
		verify(studioExists,times(1)).exists(Mockito.anyString());
   }

}
