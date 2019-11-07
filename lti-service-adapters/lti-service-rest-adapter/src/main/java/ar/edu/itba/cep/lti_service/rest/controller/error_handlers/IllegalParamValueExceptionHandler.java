/*
 * Copyright 2018-2019 BellotApps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.edu.itba.cep.lti_service.rest.controller.error_handlers;


import ar.edu.itba.cep.lti_service.services.LtiBadRequestException;
import com.bellotapps.utils.error_handler.ExceptionHandler;
import com.bellotapps.utils.error_handler.ExceptionHandlerObject;
import com.bellotapps.utils.error_handler.HandlingResult;
import com.bellotapps.webapps_commons.constants.HttpStatus;
import com.bellotapps.webapps_commons.web.dtos.api_errors.ClientErrorDto;


/**
 * {@link ExceptionHandler} in charge of handling {@link LtiBadRequestException}.
 * Will result into a <b>400 Bad Request</b> response.
 */
@ExceptionHandlerObject
public class IllegalParamValueExceptionHandler
        implements ExceptionHandler<LtiBadRequestException, IllegalParamValueExceptionHandler.LtiBadRequestErrorDto> {

    @Override
    public HandlingResult<LtiBadRequestErrorDto> handle(final LtiBadRequestException exception) {
        return HandlingResult
                .withPayload(HttpStatus.BAD_REQUEST.getCode(), LtiBadRequestErrorDto.getInstance());
    }

    /**
     * Data transfer object for client errors caused when setting an illegal value to a param (path or query).
     */
    public static class LtiBadRequestErrorDto extends ClientErrorDto {

        /**
         * Constructor.
         */
        public LtiBadRequestErrorDto() {
            super(ErrorFamily.REPRESENTATION);
        }

        /**
         * The singleton.
         */
        private static final LtiBadRequestErrorDto INSTANCE = new LtiBadRequestErrorDto();

        /**
         * @return The singleton instance.
         */
        private static LtiBadRequestErrorDto getInstance() {
            return INSTANCE;
        }
    }
}
