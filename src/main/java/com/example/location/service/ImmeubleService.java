package com.example.location.service;

import com.example.location.dto.ImmeubleDto;
import java.util.List;

public interface ImmeubleService {
    List<ImmeubleDto> lister();
    ImmeubleDto creer(ImmeubleDto dto);
}
