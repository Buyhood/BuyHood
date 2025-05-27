package api.buyhood.global.common.exception.enums;

import api.buyhood.global.common.exception.BaseException;

public class BadRequestException extends BaseException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
