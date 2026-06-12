package org.openlmis.integration.dhis2.service.indicator;

import static org.openlmis.integration.dhis2.i18n.MessageKeys.ERROR_ENUMERATOR_NOT_EXIST;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import org.openlmis.integration.dhis2.domain.enumerator.IndicatorEnum;
import org.openlmis.integration.dhis2.exception.ValidationMessageException;
import org.openlmis.integration.dhis2.repository.indicator.StockAdjustmentsAndTransfersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class MaximumStockQuantity implements IndicatorSupplier {

  public static final String NAME = IndicatorEnum.MAXIMUM_STOCK_QUANTITY.toString();

  @Autowired
  private StockAdjustmentsAndTransfersRepository stockAdjustmentsAndTransfersRepository;


  public String getIndicatorName() {
    return NAME;
  }

  /**
   * Retrieves the maximum stock quantity.
   */
  public BigDecimal calculateValue(String source, Pair<ZonedDateTime, ZonedDateTime> period,
                                   String orderable, String facility) {
    Double calculatedIndicator;
    if (source.equals(STOCKMANAGEMENT)) {
      calculatedIndicator = stockAdjustmentsAndTransfersRepository
          .findMaximumStockQuantity(period.getFirst(), orderable, facility);
    } else {
      throw new ValidationMessageException(ERROR_ENUMERATOR_NOT_EXIST);
    }
    return new BigDecimal(calculatedIndicator.toString(), MathContext.DECIMAL64);
  }
}
