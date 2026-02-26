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

package org.openlmis.integration.dhis2.service.indicator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.openlmis.integration.dhis2.domain.enumerator.IndicatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class IndicatorService {

  private final Map<String, IndicatorSupplier> supplierMap;

  @Autowired
  public IndicatorService(List<IndicatorSupplier> suppliers) {
    supplierMap = suppliers.stream()
            .collect(Collectors.toMap(IndicatorSupplier::getIndicatorName, Function.identity()));
  }

  /**
   * Counts quantity of items for a given indicator enumerator.
   *
   * @param indicatorEnum Enumerator used to calculate indicators
   * @param period Pair containing starting date and end date
   * @return BigDecimal with generated indicator value for a given period
   */
  public BigDecimal generate(String source, IndicatorEnum indicatorEnum, Pair<ZonedDateTime,
          ZonedDateTime> period, String orderable, String facility) {
    IndicatorSupplier supplier = supplierMap.get(indicatorEnum.toString());
    if (supplier == null) {
      return new BigDecimal("0", MathContext.DECIMAL64);
    }
    return supplier.calculateValue(source, period, orderable, facility);
  }

  /**
   * Counts quantity of items for a given indicator enumerator.
   *
   * @param indicatorName Enumerator name used to calculate indicators
   * @param period Pair containing starting date and end date
   * @return BigDecimal with generated indicator value for a given period
   */
  public BigDecimal generate(String source, String indicatorName, Pair<ZonedDateTime,
          ZonedDateTime> period, String orderable, String facility) {
    return generate(source, fromString(indicatorName), period, orderable, facility);
  }

  /**
   * Create enumerator from string.
   */
  public IndicatorEnum fromString(String name) {
    try {
      return IndicatorEnum.valueOf(name.replaceAll(" ", "_").toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(String.format(
              "There is no enumerator with name '%s'", name), ex);
    }
  }

}
