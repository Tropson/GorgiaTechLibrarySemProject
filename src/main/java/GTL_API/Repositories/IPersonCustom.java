package GTL_API.Repositories;

import GTL_API.Models.Entities.PersonEntity;
import GTL_API.Models.ReturnModels.CoverTypeReturn;
import GTL_API.Models.ReturnModels.PersonReturn;

public interface IPersonCustom {
    PersonReturn findPersonBySsn(String ssn);
    PersonReturn findPersonByFirstNameAndLastName(String firstName, String lastName);
    PersonReturn findPersonByCardNumberId(Integer cardNumberId);

    PersonReturn updatePerson(PersonEntity personEntity);

    PersonReturn createPerson(PersonEntity personEntity);
}
