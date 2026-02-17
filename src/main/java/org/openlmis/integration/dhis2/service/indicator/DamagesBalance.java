package org.openlmis.integration.dhis2.service.indicator;

import org.openlmis.integration.dhis2.domain.enumerator.IndicatorEnum;
import org.openlmis.integration.dhis2.exception.ValidationMessageException;
import org.openlmis.integration.dhis2.repository.indicator.StockmanagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;

import static org.openlmis.integration.dhis2.i18n.MessageKeys.ERROR_ENUMERATOR_NOT_EXIST;

@Component
public class DamagesBalance implements IndicatorSupplier {

    public static final String NAME = IndicatorEnum.DAMAGES.toString();

    @Autowired
    private StockmanagementRepository stockmanagementRepository;

    public String getIndicatorName() {
        return NAME;
    }

    /**
     * Calculate the quantity of damaged stock
     * @param source
     * @param period
     * @param facility
     * @param orderable
     * @return
     */
    @Override
    public BigDecimal calculateValue(String source, Pair<ZonedDateTime, ZonedDateTime> period,
                                     String facility, String orderable) {
        Double calculatedIndicator;
        if (source.equals(STOCKMANAGEMENT)) {
            calculatedIndicator = stockmanagementRepository.findDamaged(
                    period.getFirst(), period.getSecond(), orderable, facility);
        } else {
            throw new ValidationMessageException(ERROR_ENUMERATOR_NOT_EXIST);
        }
        return new BigDecimal(calculatedIndicator.toString(), MathContext.DECIMAL64);
    }
}
