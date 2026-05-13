package com.selloLegitimo.ConfiguracionEleccion.fraude.servicio;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.FraudEvaluationResult;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;

public interface FraudEngineService {

	FraudEvaluationResult evaluateEvent(VotingEventContext eventContext);
}
