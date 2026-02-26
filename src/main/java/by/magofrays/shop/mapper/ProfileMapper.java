package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.CreateUpdateProfileDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.entity.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ReadProfileDto toDto(Profile profile);
    Profile toEntity(CreateUpdateProfileDto profileDto);
}
