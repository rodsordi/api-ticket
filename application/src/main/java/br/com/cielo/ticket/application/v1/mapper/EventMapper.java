package br.com.cielo.ticket.application.v1.mapper;

import br.com.cielo.ticket.application.v1.dto.EventDto;
import br.com.cielo.ticket.domain.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventDto.CreateRequest request);

    EventDto.Response toResponse(Event event);
}
