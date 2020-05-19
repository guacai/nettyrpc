package entity;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
@Getter
@Setter
public class Response {
    private Object msg;
    private String code;
    private Exception error;
}
