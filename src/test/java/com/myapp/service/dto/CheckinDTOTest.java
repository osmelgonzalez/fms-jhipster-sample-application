package com.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckinDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckinDTO.class);
        CheckinDTO checkinDTO1 = new CheckinDTO();
        checkinDTO1.setId(1L);
        CheckinDTO checkinDTO2 = new CheckinDTO();
        assertThat(checkinDTO1).isNotEqualTo(checkinDTO2);
        checkinDTO2.setId(checkinDTO1.getId());
        assertThat(checkinDTO1).isEqualTo(checkinDTO2);
        checkinDTO2.setId(2L);
        assertThat(checkinDTO1).isNotEqualTo(checkinDTO2);
        checkinDTO1.setId(null);
        assertThat(checkinDTO1).isNotEqualTo(checkinDTO2);
    }
}
