package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.AdvertisementDao;
import edu.netcracker.project.logistic.dao.AdvertisementTypeDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.AdvertisementType;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AdvertisementDaoImpl implements AdvertisementDao, QueryDao {

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;
    private AdvertisementTypeDao advertisementTypeDao;
    private RowMapper<AdvertisementType> advertisementTypeRowMapper;

    @Autowired
    public AdvertisementDaoImpl(JdbcTemplate jdbcTemplate,
                                QueryService queryService,
                                AdvertisementTypeDao advertisementTypeDao,
                                RowMapper<AdvertisementType> advertisementTypeRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
        this.advertisementTypeDao = advertisementTypeDao;
        this.advertisementTypeRowMapper = advertisementTypeRowMapper;
    }

    private RowMapper<Advertisement> getMapper() {
        return (resultSet, i) ->
        {
            Advertisement advertisement = new Advertisement();
            advertisement.setId(resultSet.getLong("advertisement_id"));
            advertisement.setCaption(resultSet.getString("caption"));
            advertisement.setDescription(resultSet.getString("description"));
            advertisement.setShowFirstDate(resultSet.getDate("show_first_date").toLocalDate());
            advertisement.setShowEndDate(resultSet.getDate("show_end_date").toLocalDate());
            advertisement.setImage(resultSet.getBytes("image"));

            AdvertisementType advertisementType = advertisementTypeRowMapper.mapRow(resultSet, i);
            advertisement.setType(advertisementType);

            return advertisement;
        };
    }

    @Override
    public Advertisement save(Advertisement advertisement) {

        boolean hasPrimaryKey = advertisement.getId() != null;

        AdvertisementType advertisementType = advertisement.getType();
        String advertisementTypeName = advertisementType.getName();
        Optional<AdvertisementType> type = advertisementTypeDao.getByName(advertisementTypeName);
        type.ifPresent(advertisement::setType);

        if (hasPrimaryKey){
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, advertisement.getId());
                ps.setObject(2, advertisement.getCaption());
                ps.setObject(3, advertisement.getDescription());
                ps.setObject(4, Date.valueOf(advertisement.getShowFirstDate()));
                ps.setObject(5, Date.valueOf(advertisement.getShowEndDate()));
                ps.setObject(6, advertisement.getImage());
                ps.setObject(7, advertisement.getType().getId());
            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, advertisement.getCaption());
                ps.setObject(2, advertisement.getDescription());
                ps.setObject(3, Date.valueOf(advertisement.getShowFirstDate()));
                ps.setObject(4, Date.valueOf(advertisement.getShowEndDate()));
                ps.setObject(5, advertisement.getImage());
                ps.setObject(6, advertisement.getType().getId());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("advertisement_id");
            advertisement.setId(key.longValue());
        }
        return advertisement;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(getDeleteQuery(), ps ->
        {
            ps.setObject(1, id);
        });
    }



    @Override
    public Optional<Advertisement> findOne(Long id) {

        try {
            Advertisement advertisement = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{id},
                    getMapper());
            return Optional.of(advertisement);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int getCountOfAllAdvertisements() {
        int count = jdbcTemplate.queryForObject(
                getCountOfAllAdvertisementsQuery(),
                Integer.class
        );
        return count;
    }

    @Override
    public List<Advertisement> findAmountOfAdvertisementsForCurrentPage(int itemsOnPage, int currentPage) {
        return jdbcTemplate.query(
                getAllAdvertisementsForCurrentPage(),
                new Object[] { itemsOnPage, currentPage * itemsOnPage},
                getMapper());
    }

    @Override
    public List<Advertisement> allAdvertisementsForToday() {
        LocalDate today = LocalDate.now();
        return jdbcTemplate.query(
                getAllAdvertisementsForToday(),
                new  Object[] {Date.valueOf(today), Date.valueOf(today)},
                getMapper());
    }

    @Override
    public List<Advertisement> allAdvertisements() {
        return jdbcTemplate.query(getAllAdvertisements(), getMapper());
    }

    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.advertisement");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.advertisement");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.advertisement");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.advertisement");
    }

    public String getAllAdvertisements(){
        return queryService.getQuery("all.advertisements");
    }

    public String getCountOfAllAdvertisementsQuery(){
        return queryService.getQuery("count.advertisements");
    }

    public String getAllAdvertisementsForCurrentPage(){
        return queryService.getQuery("all.advertisements.for.page");
    }

    public String getAllAdvertisementsForToday() {
        return queryService.getQuery("all.advertisements.for.today");
    }
}
