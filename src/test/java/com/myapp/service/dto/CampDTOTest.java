package com.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CampDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CampDTO.class);
        CampDTO campDTO1 = new CampDTO();
        campDTO1.setId(1L);
        CampDTO campDTO2 = new CampDTO();
        assertThat(campDTO1).isNotEqualTo(campDTO2);
        campDTO2.setId(campDTO1.getId());
        assertThat(campDTO1).isEqualTo(campDTO2);
        campDTO2.setId(2L);
        assertThat(campDTO1).isNotEqualTo(campDTO2);
        campDTO1.setId(null);
        assertThat(campDTO1).isNotEqualTo(campDTO2);
    }
}
