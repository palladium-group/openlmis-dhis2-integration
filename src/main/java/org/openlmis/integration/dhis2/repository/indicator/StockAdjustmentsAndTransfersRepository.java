/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.integration.dhis2.repository.indicator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class StockAdjustmentsAndTransfersRepository {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(StockAdjustmentsAndTransfersRepository.class);

  static final String START_DATE = "startDate";
  static final String END_DATE = "endDate";
  static final String ORDERABLE = "orderable";
  static final String FACILITY = "facility";

  @PersistenceContext
  EntityManager entityManager;


  /**
   * Retrieves received amount of products from stockmanagement for a given period.
   */
  public Double findReceived(@Param(START_DATE) ZonedDateTime startDate,
                             @Param(END_DATE) ZonedDateTime endDate,
                             @Param(ORDERABLE) String orderable,
                             @Param(FACILITY) String facility) {
    Query query = entityManager.createNativeQuery(
            "SELECT COALESCE(SUM(line_items.quantity), 0) AS quantity "
                    + "FROM stockmanagement.stock_card_line_items AS line_items  "
                    + "JOIN stockmanagement.stock_cards AS cards "
                    + "ON line_items.stockcardid = cards.id "
                    + "JOIN stockmanagement.stock_card_line_item_reasons AS reasons "
                    + "ON reasons.id = line_items.reasonid "
                    + "JOIN referencedata.orderables AS products "
                    + "ON cards.orderableid = products.id  "
                    + "JOIN referencedata.facilities AS facilities "
                    + "ON facilities.id = cards.facilityid  "
                    + "WHERE products.versionnumber = ( "
                    + "SELECT MAX(versionnumber) FROM referencedata.orderables o2 "
                    + "WHERE o2.id = products.id "
                    + ") "
                    + "AND reasons.reasoncategory = 'TRANSFER' "
                    + "AND reasons.reasontype = 'CREDIT' "
                    + "AND line_items.occurreddate >= :startDate "
                    + "AND line_items.occurreddate < :endDate "
                    + "AND products.code ILIKE :orderable  "
                    + "AND facilities.code = :facility ");

    return Double.parseDouble(query.setParameter(START_DATE, startDate)
            .setParameter(END_DATE, endDate)
            .setParameter(ORDERABLE, orderable + "%")
            .setParameter(FACILITY, facility)
            .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all positive adjustments from stockmanagement for a given period.
   */
  public Double findPositiveAdjustments(@Param(START_DATE) ZonedDateTime startDate,
                                        @Param(END_DATE) ZonedDateTime endDate,
                                        @Param(ORDERABLE) String orderable,
                                        @Param(FACILITY) String facility) {
    Query query = entityManager.createNativeQuery(
            "SELECT COALESCE(SUM(line_items.quantity), 0) AS quantity "
                    + "FROM stockmanagement.stock_card_line_items AS line_items  "
                    + "JOIN stockmanagement.stock_cards AS cards "
                    + "ON line_items.stockcardid = cards.id "
                    + "JOIN stockmanagement.stock_card_line_item_reasons AS reasons "
                    + "ON reasons.id = line_items.reasonid "
                    + "JOIN referencedata.orderables AS products "
                    + "ON cards.orderableid = products.id  "
                    + "JOIN referencedata.facilities AS facilities "
                    + "ON facilities.id = cards.facilityid  "
                    + "WHERE products.versionnumber = ( "
                    + "SELECT MAX(versionnumber) FROM referencedata.orderables o2 "
                    + "WHERE o2.id = products.id "
                    + ") "
                    + "AND reasons.reasoncategory = 'ADJUSTMENT' "
                    + "AND reasons.reasontype = 'CREDIT' "
                    + "AND line_items.occurreddate >= :startDate "
                    + "AND line_items.occurreddate < :endDate "
                    + "AND products.code ILIKE :orderable  "
                    + "AND facilities.code = :facility ");

    return Double.parseDouble(query.setParameter(START_DATE, startDate)
            .setParameter(END_DATE, endDate)
            .setParameter(ORDERABLE, orderable + "%")
            .setParameter(FACILITY, facility)
            .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all negative adjustments from stockmanagement for a given period.
   */
  public Double findNegativeAdjustments(@Param(START_DATE) ZonedDateTime startDate,
                                        @Param(END_DATE) ZonedDateTime endDate,
                                        @Param(ORDERABLE) String orderable,
                                        @Param(FACILITY) String facility) {
    Query query = entityManager.createNativeQuery(
            "SELECT COALESCE(SUM(line_items.quantity), 0) AS quantity "
                    + "FROM stockmanagement.stock_card_line_items AS line_items  "
                    + "JOIN stockmanagement.stock_cards AS cards "
                    + "ON line_items.stockcardid = cards.id "
                    + "JOIN stockmanagement.stock_card_line_item_reasons AS reasons "
                    + "ON reasons.id = line_items.reasonid "
                    + "JOIN referencedata.orderables AS products "
                    + "ON cards.orderableid = products.id  "
                    + "JOIN referencedata.facilities AS facilities "
                    + "ON facilities.id = cards.facilityid  "
                    + "WHERE products.versionnumber = ( "
                    + "SELECT MAX(versionnumber) FROM referencedata.orderables o2 "
                    + "WHERE o2.id = products.id "
                    + ") "
                    + "AND reasons.reasoncategory = 'ADJUSTMENT' "
                    + "AND reasons.reasontype = 'DEBIT' "
                    + "AND line_items.occurreddate >= :startDate "
                    + "AND line_items.occurreddate < :endDate "
                    + "AND products.code ILIKE :orderable  "
                    + "AND facilities.code = :facility ");

    return Double.parseDouble(query.setParameter(START_DATE, startDate)
            .setParameter(END_DATE, endDate)
            .setParameter(ORDERABLE, orderable + "%")
            .setParameter(FACILITY, facility)
            .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all damaged stock from stockmanagement for a given period.
   */
  public Double findDamaged(@Param(START_DATE) ZonedDateTime startDate,
                            @Param(END_DATE) ZonedDateTime endDate,
                            @Param(ORDERABLE) String orderable,
                            @Param(FACILITY) String facility) {

    Query query = entityManager.createNativeQuery(
            "select COALESCE(SUM(line_items.quantity), 0) as quantity "
                    + "from stockmanagement.stock_card_line_items line_items "
                    + "join stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
                    + "join stockmanagement.stock_card_line_item_reasons reasons "
                    + "ON reasons.id = line_items.reasonid "
                    + "join referencedata.orderables products ON products.id = cards.orderableid "
                    + "JOIN referencedata.facilities as facilities "
                    + "on facilities.id = cards.facilityid "
                    + "where LOWER(reasons.name) LIKE '%damage%' "
                    + "AND line_items.occurreddate >= :startDate "
                    + "AND line_items.occurreddate < :endDate "
                    + "AND products.code ILIKE :orderable "
                    + "AND facilities.code = :facility "
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate)
            .setParameter(END_DATE, endDate)
            .setParameter(ORDERABLE, orderable + "%")
            .setParameter(FACILITY, facility)
            .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all expired stock from stockmanagement for a given period.
   */
  public Double findExpired(@Param(START_DATE) ZonedDateTime startDate,
                            @Param(END_DATE) ZonedDateTime endDate,
                            @Param(ORDERABLE) String orderable,
                            @Param(FACILITY) String facility) {

    Query query = entityManager.createNativeQuery(
        "select COALESCE(SUM(line_items.quantity), 0)  AS quantity "
            + "FROM stockmanagement.stock_card_line_items line_items "
            + "JOIN stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
            + "JOIN stockmanagement.stock_card_line_item_reasons reasons "
            + "ON reasons.id = line_items.reasonid "
            + "JOIN referencedata.orderables products ON products.id = cards.orderableid "
            + "JOIN referencedata.facilities as facilities "
            + "on facilities.id = cards.facilityid "
            + "WHERE LOWER(reasons.name) LIKE '%expir%' "
            + "AND line_items.occurreddate >= :startDate "
            + "AND line_items.occurreddate < :endDate "
            + "AND products.code ILIKE :orderable "
            + "AND facilities.code = :facility"
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate)
        .setParameter(END_DATE, endDate)
        .setParameter(ORDERABLE, orderable + "%")
        .setParameter(FACILITY, facility)
        .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all transfer-in commodities  for a given period.
   */
  public Double findTransferIns(@Param(START_DATE) ZonedDateTime startDate,
                            @Param(END_DATE) ZonedDateTime endDate,
                            @Param(ORDERABLE) String orderable,
                            @Param(FACILITY) String facility) {

    Query query = entityManager.createNativeQuery(
        "select COALESCE(SUM(line_items.quantity), 0)  AS quantity "
            + "FROM stockmanagement.stock_card_line_items line_items "
            + "JOIN stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
            + "JOIN stockmanagement.stock_card_line_item_reasons reasons "
            + "ON reasons.id = line_items.reasonid "
            + "JOIN referencedata.orderables products ON products.id = cards.orderableid "
            + "JOIN referencedata.facilities as facilities "
            + "on facilities.id = cards.facilityid "
            + "WHERE line_items.sourceid is not null "
            + "AND line_items.occurreddate >= :startDate "
            + "AND line_items.occurreddate < :endDate "
            + "AND products.code ILIKE :orderable "
            + "AND facilities.code = :facility"
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate)
        .setParameter(END_DATE, endDate)
        .setParameter(ORDERABLE, orderable + "%")
        .setParameter(FACILITY, facility)
        .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all transfer-out commodities  for a given period.
   */
  public Double findTransferOuts(@Param(START_DATE) ZonedDateTime startDate,
                                @Param(END_DATE) ZonedDateTime endDate,
                                @Param(ORDERABLE) String orderable,
                                @Param(FACILITY) String facility) {

    Query query = entityManager.createNativeQuery(
        "select COALESCE(SUM(line_items.quantity), 0)  AS quantity "
            + "FROM stockmanagement.stock_card_line_items line_items "
            + "JOIN stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
            + "JOIN stockmanagement.stock_card_line_item_reasons reasons "
            + "ON reasons.id = line_items.reasonid "
            + "JOIN referencedata.orderables products ON products.id = cards.orderableid "
            + "JOIN referencedata.facilities as facilities "
            + "on facilities.id = cards.facilityid "
            + "WHERE line_items.destinationid is not null "
            + "AND line_items.occurreddate >= :startDate "
            + "AND line_items.occurreddate < :endDate "
            + "AND products.code ILIKE :orderable "
            + "AND facilities.code = :facility"
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate)
        .setParameter(END_DATE, endDate)
        .setParameter(ORDERABLE, orderable + "%")
        .setParameter(FACILITY, facility)
        .getSingleResult().toString());
  }

  /**
   * Retrieves sum of all consumed commodities for a given period.
   */
  public Double findConsumption(@Param(START_DATE) ZonedDateTime startDate,
                                @Param(END_DATE) ZonedDateTime endDate,
                                @Param(ORDERABLE) String orderable,
                                @Param(FACILITY) String facility) {
    Query query = entityManager.createNativeQuery(
        "select COALESCE(SUM(line_items.quantity), 0)  AS quantity "
            + "FROM stockmanagement.stock_card_line_items line_items "
            + "JOIN stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
            + "JOIN stockmanagement.stock_card_line_item_reasons reasons ON "
            + "reasons.id = line_items.reasonid "
            + "JOIN referencedata.orderables products ON products.id = cards.orderableid "
            + "JOIN referencedata.facilities as facilities on facilities.id = cards.facilityid "
            + "WHERE (reasons.name = 'Consumed' or reasons.name = 'Internal Transfer') "
            + "AND line_items.occurreddate >= :startDate "
            + "AND line_items.occurreddate < :endDate "
            + "AND products.code ILIKE :orderable "
            + "AND facilities.code = :facility"
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate)
        .setParameter(END_DATE, endDate)
        .setParameter(ORDERABLE, orderable + "%")
        .setParameter(FACILITY, facility)
        .getSingleResult().toString());
  }

  /**
   * Retrieves the mean of consumed commodities over 3 months.
   */
  public Double findAverageConsumption(@Param(END_DATE) ZonedDateTime startDate,
                                       @Param(ORDERABLE) String orderable,
                                       @Param(FACILITY) String facility) {
    LOGGER.debug("startDate: {}, orderable: {}, facility: {}", startDate, orderable, facility);
    LOGGER.debug("Current timezone is: {}", TimeZone.getDefault().getID());
    LOGGER.debug("Current Zone is: {}", ZoneId.systemDefault().getId());
    Query query = entityManager.createNativeQuery(
        "select ROUND((COALESCE(SUM(line_items.quantity), 0)/3), 2) AS quantity "
            + "FROM stockmanagement.stock_card_line_items line_items "
            + "JOIN stockmanagement.stock_cards cards ON cards.id = line_items.stockcardid "
            + "JOIN stockmanagement.stock_card_line_item_reasons reasons ON "
            + "reasons.id = line_items.reasonid "
            + "JOIN referencedata.orderables products ON products.id = cards.orderableid "
            + "JOIN referencedata.facilities as facilities on facilities.id = cards.facilityid "
            + "WHERE (reasons.name = 'Consumed' or reasons.name = 'Internal Transfer') "
            + "AND line_items.occurreddate >="
            + " (date_trunc('month', cast(:startDate as date)) - interval '3 months') "
            + "AND line_items.occurreddate < date_trunc('month', cast(:startDate as date)) "
            + "AND products.code ILIKE :orderable "
            + "AND facilities.code = :facility"
    );
    return Double.parseDouble(query.setParameter(START_DATE, startDate.toLocalDate())
        .setParameter(ORDERABLE, orderable + "%")
        .setParameter(FACILITY, facility)
        .getSingleResult().toString());
  }
}
