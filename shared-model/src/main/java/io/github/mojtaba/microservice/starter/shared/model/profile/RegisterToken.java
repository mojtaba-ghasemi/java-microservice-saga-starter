package io.github.mojtaba.microservice.starter.shared.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.mojtaba.microservice.starter.shared.model.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterToken {
    private String sendOTPId;
    private String sentOtp;
    private String customerId;
    private Date createDate;
    private Date expirationDate;
    private String mobile;
    private String deviceId;
    private Platform platform;
    private String version;
    private String customerSessionId;
    private String userIP;
    private String nationalCode;

    public RegisterToken(String sendOTPId, String sentOtp, String mobile) {
        this.sendOTPId = sendOTPId;
        this.sentOtp = sentOtp;
        this.mobile = mobile;
    }

    public RegisterToken(String sendOTPId, String sentOtp, String mobile, String deviceId, Platform platform,
                         String version, String userIP) {
        this(sendOTPId, sentOtp, mobile);
        this.deviceId = deviceId;
        this.platform = platform;
        this.version = version;
        this.userIP = userIP;
    }

    public RegisterToken(String sendOTPId, String sentOtp, String mobile, String deviceId, Platform platform,
                         String version, String userIP, String nationalCode) {
        this(sendOTPId, sentOtp, mobile);
        this.deviceId = deviceId;
        this.platform = platform;
        this.version = version;
        this.userIP = userIP;
        this.nationalCode = nationalCode;
    }

    public RegisterToken(String customerId, String mobile, String deviceId, Platform platform, String version, String customerSessionId, String nationalCode) {
        this.customerId = customerId;
        this.mobile = mobile;
        this.deviceId = deviceId;
        this.platform = platform;
        this.version = version;
        this.customerSessionId=customerSessionId;
        this.nationalCode = nationalCode;
    }

    public RegisterToken(String customerId, String mobile, String deviceId, Platform platform, String version,
                         String customerSessionId, String userIP, String nationalCode) {
        this.customerId = customerId;
        this.mobile = mobile;
        this.deviceId = deviceId;
        this.platform = platform;
        this.version = version;
        this.customerSessionId=customerSessionId;
        this.userIP = userIP;
        this.nationalCode = nationalCode;
    }
}

