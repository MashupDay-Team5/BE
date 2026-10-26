package com.mashup_team5.domain.hospital.repository;

import java.util.List;

public record HospitalIdSlice(List<Long> hospitalIds, boolean hasNext) {
}
