package edu.netcracker.project.logistic.service.impl;

import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.CourierStatus;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Route;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    private PasswordEncoder passwordEncoder;
    private PersonCrudDao personCrudDao;
    private CourierDataDao courierDataDao;

    @Autowired
    public PersonServiceImpl(PersonCrudDao personCrudDao, CourierDataDao courierDataDao) {
        this.personCrudDao = personCrudDao;
        this.courierDataDao = courierDataDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void savePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personCrudDao.save(person);
        boolean hasCourierRole =
                person.getRoles().stream()
                        .anyMatch(r -> r.getRoleName().equals("ROLE_COURIER"));
        if (hasCourierRole) {
            CourierData data = new CourierData();
            data.setCourierStatus(CourierStatus.FREE);
            data.setCourier(person);
            data.setRoute(new Route());
            data.setTravelMode(TravelMode.WALKING);
            courierDataDao.save(data);
        }
    }

    @Override
    public void delete(Long aLong) {
        personCrudDao.delete(aLong);
    }

    @Override
    public Optional<Person> findOne(Long aLong) {
        return personCrudDao.findOne(aLong);
    }

    @Override
    public Optional<Person> findOne(String username) {
        return personCrudDao.findOne(username);
    }

    @Override
    public boolean exists(Long aLong) {
        return personCrudDao.contains(aLong);
    }

    @Override
    public Optional<Person> findOneByEmail(String email) {
        return personCrudDao.findOneByEmail(email);
    }
}
