package GTL_API.Controllers;

import GTL_API.Models.CreationModels.PersonCreation;
import GTL_API.Models.ReturnModels.PersonReturn;
import GTL_API.Models.UpdateModels.PersonUpdate;
import GTL_API.Services.PersonService.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("gtl/person")
public class PersonController {
    private IPersonService iPersonService;

    @Autowired
    public void setIPersonService(IPersonService iPersonService){
        this.iPersonService = iPersonService;
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH)
    public ResponseEntity<?> updatePersonBySsn(@RequestBody @Valid PersonUpdate person){
        return new ResponseEntity<>(iPersonService.updatePerson(person), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createPerson(@RequestBody @Valid PersonCreation person){
        return new ResponseEntity<>(iPersonService.createPerson(person), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/findbyname/{firstname}/{lastname}", method = RequestMethod.GET)
    public ResponseEntity<?> findPersonByFirstNameAndLastName(@PathVariable String firstname, @PathVariable String lastname){
        return new ResponseEntity<>(iPersonService.findPersonByFirstNameAndLastName(firstname, lastname), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/findbycard/{number}", method = RequestMethod.GET)
    public ResponseEntity<?> findPersonByCardNumber(@PathVariable Integer number){
        return new ResponseEntity<>(iPersonService.findPersonByCardNumberId(number), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/findbyssn/{ssn}", method = RequestMethod.GET)
    public ResponseEntity<?> findPersonBySsn(@PathVariable String ssn){
        return new ResponseEntity<>(iPersonService.findPersonBySsn(ssn), new HttpHeaders(), HttpStatus.OK);
    }
}
