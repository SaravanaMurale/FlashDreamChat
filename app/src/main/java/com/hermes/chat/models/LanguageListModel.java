package com.hermes.chat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageListModel {

    @SerializedName("splash")
    @Expose
    private Splash splash;
    @SerializedName("loginpage")
    @Expose
    private Loginpage loginpage;
    @SerializedName("homepage")
    @Expose
    private Homepage homepage;
    @SerializedName("chatpage")
    @Expose
    private Chatpage chatpage;
    @SerializedName("grouppage")
    @Expose
    private Grouppage grouppage;
    @SerializedName("profiledetail")
    @Expose
    private Profiledetail profiledetail;
    @SerializedName("creategrouppage")
    @Expose
    private Creategrouppage creategrouppage;
    @SerializedName("statuspage")
    @Expose
    private Statuspage statuspage;
    @SerializedName("callspage")
    @Expose
    private Callspage callspage;
    @SerializedName("settingspage")
    @Expose
    private Settingspage settingspage;
    @SerializedName("changepasswordpage")
    @Expose
    private ChangePasswordPage changepasswordpage;

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("schedulepage")
    @Expose
    private Schedulepage schedulepage;
    @SerializedName("passcodepage")
    @Expose
    private Passcodepage passcodepage;

    public Passcodepage getPasscodepage() {
        return passcodepage;
    }

    public void setPasscodepage(Passcodepage passcodepage) {
        this.passcodepage = passcodepage;
    }

    public Schedulepage getSchedulepage() {
        return schedulepage;
    }

    public void setSchedulepage(Schedulepage schedulepage) {
        this.schedulepage = schedulepage;
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public Splash getSplash() {
        return splash;
    }

    public void setSplash(Splash splash) {
        this.splash = splash;
    }

    public Loginpage getLoginpage() {
        return loginpage;
    }

    public void setLoginpage(Loginpage loginpage) {
        this.loginpage = loginpage;
    }

    public Homepage getHomepage() {
        return homepage;
    }

    public void setHomepage(Homepage homepage) {
        this.homepage = homepage;
    }

    public Chatpage getChatpage() {
        return chatpage;
    }

    public void setChatpage(Chatpage chatpage) {
        this.chatpage = chatpage;
    }

    public Grouppage getGrouppage() {
        return grouppage;
    }

    public void setGrouppage(Grouppage grouppage) {
        this.grouppage = grouppage;
    }

    public Profiledetail getProfiledetail() {
        return profiledetail;
    }

    public void setProfiledetail(Profiledetail profiledetail) {
        this.profiledetail = profiledetail;
    }

    public Creategrouppage getCreategrouppage() {
        return creategrouppage;
    }

    public void setCreategrouppage(Creategrouppage creategrouppage) {
        this.creategrouppage = creategrouppage;
    }

    public Statuspage getStatuspage() {
        return statuspage;
    }

    public void setStatuspage(Statuspage statuspage) {
        this.statuspage = statuspage;
    }

    public Callspage getCallspage() {
        return callspage;
    }

    public void setCallspage(Callspage callspage) {
        this.callspage = callspage;
    }

    public Settingspage getSettingspage() {
        return settingspage;
    }

    public void setSettingspage(Settingspage settingspage) {
        this.settingspage = settingspage;
    }

    public ChangePasswordPage getChangepasswordpage() {
        return changepasswordpage;
    }

    public void setChangepasswordpage(ChangePasswordPage changepasswordpage) {
        this.changepasswordpage = changepasswordpage;
    }

    public LanguageListModel() {
    }

    public class Common {
        public Common() {
        }

        @SerializedName("lbl_camera")
        @Expose
        private String lblCamera;
        @SerializedName("lbl_cancel")
        @Expose
        private String lblCancel;
        @SerializedName("lbl_clear_all")
        @Expose
        private String lblClearAll;
        @SerializedName("lbl_done")
        @Expose
        private String lblDone;
        @SerializedName("lbl_media")
        @Expose
        private String lblMedia;
        @SerializedName("lbl_more")
        @Expose
        private String lblMore;
        @SerializedName("lbl_no")
        @Expose
        private String lblNo;
        @SerializedName("lbl_photo")
        @Expose
        private String lblPhoto;
        @SerializedName("lbl_select_language")
        @Expose
        private String lblSelectLanguage;
        @SerializedName("lbl_yes")
        @Expose
        private String lblYes;

        public String getLblCamera() {
            return lblCamera;
        }

        public void setLblCamera(String lblCamera) {
            this.lblCamera = lblCamera;
        }

        public String getLblCancel() {
            return lblCancel;
        }

        public void setLblCancel(String lblCancel) {
            this.lblCancel = lblCancel;
        }

        public String getLblClearAll() {
            return lblClearAll;
        }

        public void setLblClearAll(String lblClearAll) {
            this.lblClearAll = lblClearAll;
        }

        public String getLblDone() {
            return lblDone;
        }

        public void setLblDone(String lblDone) {
            this.lblDone = lblDone;
        }

        public String getLblMedia() {
            return lblMedia;
        }

        public void setLblMedia(String lblMedia) {
            this.lblMedia = lblMedia;
        }

        public String getLblMore() {
            return lblMore;
        }

        public void setLblMore(String lblMore) {
            this.lblMore = lblMore;
        }

        public String getLblNo() {
            return lblNo;
        }

        public void setLblNo(String lblNo) {
            this.lblNo = lblNo;
        }

        public String getLblPhoto() {
            return lblPhoto;
        }

        public void setLblPhoto(String lblPhoto) {
            this.lblPhoto = lblPhoto;
        }

        public String getLblSelectLanguage() {
            return lblSelectLanguage;
        }

        public void setLblSelectLanguage(String lblSelectLanguage) {
            this.lblSelectLanguage = lblSelectLanguage;
        }

        public String getLblYes() {
            return lblYes;
        }

        public void setLblYes(String lblYes) {
            this.lblYes = lblYes;
        }

    }

    public static class Splash {
        public Splash() {
        }

        @SerializedName("lbl_loading")
        @Expose
        private String lblLoading;
        @SerializedName("lbl_always")
        @Expose
        private String lblAlways;

        public String getLblLoading() {
            return lblLoading;
        }

        public void setLblLoading(String lblLoading) {
            this.lblLoading = lblLoading;
        }

        public String getLblAlways() {
            return lblAlways;
        }

        public void setLblAlways(String lblAlways) {
            this.lblAlways = lblAlways;
        }
    }

    public static class Loginpage {
        public Loginpage() {
        }

        @SerializedName("err_enter_email")
        @Expose
        private String errEnterEmail;
        @SerializedName("err_enter_otp")
        @Expose
        private String errEnterOtp;
        @SerializedName("err_enter_phone_number")
        @Expose
        private String errEnterPhoneNumber;
        @SerializedName("err_enter_valid_email")
        @Expose
        private String errEnterValidEmail;
        @SerializedName("err_invalid_mob_no")
        @Expose
        private String errInvalidMobNo;
        @SerializedName("err_mob_already_registered")
        @Expose
        private String errMobAlreadyRegistered;
        @SerializedName("err_otp_sent")
        @Expose
        private String errOtpSent;
        @SerializedName("err_password_not_correct")
        @Expose
        private String errPasswordNotCorrect;
        @SerializedName("err_select_country_code")
        @Expose
        private String errSelectCountryCode;
        @SerializedName("err_user_not_found")
        @Expose
        private String errUserNotFound;
        @SerializedName("err_username_already_exist")
        @Expose
        private String errUsernameAlreadyExist;
        @SerializedName("lbl_accept")
        @Expose
        private String lblAccept;
        @SerializedName("lbl_cancel_verify")
        @Expose
        private String lblCancelVerify;
        @SerializedName("lbl_country")
        @Expose
        private String lblCountry;
        @SerializedName("lbl_edit")
        @Expose
        private String lblEdit;
        @SerializedName("lbl_email")
        @Expose
        private String lblEmail;
        @SerializedName("lbl_enter_loading")
        @Expose
        private String lblEnterLoading;
        @SerializedName("lbl_enter_mobileno")
        @Expose
        private String lblEnterMobileno;
        @SerializedName("lbl_enter_passowrd")
        @Expose
        private String lblEnterPassowrd;
        @SerializedName("lbl_enter_username")
        @Expose
        private String lblEnterUsername;
        @SerializedName("lbl_forgot_password")
        @Expose
        private String lblForgotPassword;
        @SerializedName("lbl_incorrect")
        @Expose
        private String lblIncorrect;
        @SerializedName("lbl_invalid_otp")
        @Expose
        private String lblInvalidOtp;
        @SerializedName("lbl_logging_you_in")
        @Expose
        private String lblLoggingYouIn;
        @SerializedName("lbl_login")
        @Expose
        private String lblLogin;
        @SerializedName("lbl_login_code")
        @Expose
        private String lblLoginCode;
        @SerializedName("lbl_no")
        @Expose
        private String lblNo;
        @SerializedName("lbl_not_receive_code")
        @Expose
        private String lblNotReceiveCode;
        @SerializedName("lbl_otp_sent_to")
        @Expose
        private String lblOtpSentTo;
        @SerializedName("lbl_password")
        @Expose
        private String lblPassword;
        @SerializedName("lbl_password_success")
        @Expose
        private String lblPasswordSuccess;
        @SerializedName("lbl_please_wait")
        @Expose
        private String lblPleaseWait;
        @SerializedName("lbl_privacy")
        @Expose
        private String lblPrivacy;
        @SerializedName("lbl_register")
        @Expose
        private String lblRegister;
        @SerializedName("lbl_resend_code")
        @Expose
        private String lblResendCode;
        @SerializedName("lbl_resend_otp")
        @Expose
        private String lblResendOtp;
        @SerializedName("lbl_select_country")
        @Expose
        private String lblSelectCountry;
        @SerializedName("lbl_sending_otp")
        @Expose
        private String lblSendingOtp;
        @SerializedName("lbl_sent_to")
        @Expose
        private String lblSentTo;
        @SerializedName("lbl_username")
        @Expose
        private String lblUsername;
        @SerializedName("lbl_verify_inprogress")
        @Expose
        private String lblVerifyInprogress;
        @SerializedName("lbl_verifying_otp")
        @Expose
        private String lblVerifyingOtp;
        @SerializedName("lbl_wrong_msg")
        @Expose
        private String lblWrongMsg;
        @SerializedName("lbl_yes")
        @Expose
        private String lblYes;
        @SerializedName("lbl_your_mob_no")
        @Expose
        private String lblYourMobNo;
        @SerializedName("lbl_default_status")
        @Expose
        private String lblDefaultStatus;
        @SerializedName("lbl_terms_conditions")
        @Expose
        private String lbl_terms_conditions;
        @SerializedName("lbl_i_accept_terms_conditions")
        @Expose
        private String lbl_i_accept_terms_conditions;

        public String getLbl_terms_conditions() {
            return lbl_terms_conditions;
        }

        public String getLbl_i_accept_terms_conditions() {
            return lbl_i_accept_terms_conditions;
        }

        public String getErrEnterEmail() {
            return errEnterEmail;
        }

        public void setErrEnterEmail(String errEnterEmail) {
            this.errEnterEmail = errEnterEmail;
        }

        public String getErrEnterOtp() {
            return errEnterOtp;
        }

        public void setErrEnterOtp(String errEnterOtp) {
            this.errEnterOtp = errEnterOtp;
        }

        public String getErrEnterPhoneNumber() {
            return errEnterPhoneNumber;
        }

        public void setErrEnterPhoneNumber(String errEnterPhoneNumber) {
            this.errEnterPhoneNumber = errEnterPhoneNumber;
        }

        public String getErrEnterValidEmail() {
            return errEnterValidEmail;
        }

        public void setErrEnterValidEmail(String errEnterValidEmail) {
            this.errEnterValidEmail = errEnterValidEmail;
        }

        public String getErrInvalidMobNo() {
            return errInvalidMobNo;
        }

        public void setErrInvalidMobNo(String errInvalidMobNo) {
            this.errInvalidMobNo = errInvalidMobNo;
        }

        public String getErrMobAlreadyRegistered() {
            return errMobAlreadyRegistered;
        }

        public void setErrMobAlreadyRegistered(String errMobAlreadyRegistered) {
            this.errMobAlreadyRegistered = errMobAlreadyRegistered;
        }

        public String getErrOtpSent() {
            return errOtpSent;
        }

        public void setErrOtpSent(String errOtpSent) {
            this.errOtpSent = errOtpSent;
        }

        public String getErrPasswordNotCorrect() {
            return errPasswordNotCorrect;
        }

        public void setErrPasswordNotCorrect(String errPasswordNotCorrect) {
            this.errPasswordNotCorrect = errPasswordNotCorrect;
        }

        public String getErrSelectCountryCode() {
            return errSelectCountryCode;
        }

        public void setErrSelectCountryCode(String errSelectCountryCode) {
            this.errSelectCountryCode = errSelectCountryCode;
        }

        public String getErrUserNotFound() {
            return errUserNotFound;
        }

        public void setErrUserNotFound(String errUserNotFound) {
            this.errUserNotFound = errUserNotFound;
        }

        public String getErrUsernameAlreadyExist() {
            return errUsernameAlreadyExist;
        }

        public void setErrUsernameAlreadyExist(String errUsernameAlreadyExist) {
            this.errUsernameAlreadyExist = errUsernameAlreadyExist;
        }

        public String getLblAccept() {
            return lblAccept;
        }

        public void setLblAccept(String lblAccept) {
            this.lblAccept = lblAccept;
        }

        public String getLblCancelVerify() {
            return lblCancelVerify;
        }

        public void setLblCancelVerify(String lblCancelVerify) {
            this.lblCancelVerify = lblCancelVerify;
        }

        public String getLblCountry() {
            return lblCountry;
        }

        public void setLblCountry(String lblCountry) {
            this.lblCountry = lblCountry;
        }

        public String getLblEdit() {
            return lblEdit;
        }

        public void setLblEdit(String lblEdit) {
            this.lblEdit = lblEdit;
        }

        public String getLblEmail() {
            return lblEmail;
        }

        public void setLblEmail(String lblEmail) {
            this.lblEmail = lblEmail;
        }

        public String getLblEnterLoading() {
            return lblEnterLoading;
        }

        public void setLblEnterLoading(String lblEnterLoading) {
            this.lblEnterLoading = lblEnterLoading;
        }

        public String getLblEnterMobileno() {
            return lblEnterMobileno;
        }

        public void setLblEnterMobileno(String lblEnterMobileno) {
            this.lblEnterMobileno = lblEnterMobileno;
        }

        public String getLblEnterPassowrd() {
            return lblEnterPassowrd;
        }

        public void setLblEnterPassowrd(String lblEnterPassowrd) {
            this.lblEnterPassowrd = lblEnterPassowrd;
        }

        public String getLblEnterUsername() {
            return lblEnterUsername;
        }

        public void setLblEnterUsername(String lblEnterUsername) {
            this.lblEnterUsername = lblEnterUsername;
        }

        public String getLblForgotPassword() {
            return lblForgotPassword;
        }

        public void setLblForgotPassword(String lblForgotPassword) {
            this.lblForgotPassword = lblForgotPassword;
        }

        public String getLblIncorrect() {
            return lblIncorrect;
        }

        public void setLblIncorrect(String lblIncorrect) {
            this.lblIncorrect = lblIncorrect;
        }

        public String getLblInvalidOtp() {
            return lblInvalidOtp;
        }

        public void setLblInvalidOtp(String lblInvalidOtp) {
            this.lblInvalidOtp = lblInvalidOtp;
        }

        public String getLblLoggingYouIn() {
            return lblLoggingYouIn;
        }

        public void setLblLoggingYouIn(String lblLoggingYouIn) {
            this.lblLoggingYouIn = lblLoggingYouIn;
        }

        public String getLblLogin() {
            return lblLogin;
        }

        public void setLblLogin(String lblLogin) {
            this.lblLogin = lblLogin;
        }

        public String getLblLoginCode() {
            return lblLoginCode;
        }

        public void setLblLoginCode(String lblLoginCode) {
            this.lblLoginCode = lblLoginCode;
        }

        public String getLblNo() {
            return lblNo;
        }

        public void setLblNo(String lblNo) {
            this.lblNo = lblNo;
        }

        public String getLblNotReceiveCode() {
            return lblNotReceiveCode;
        }

        public void setLblNotReceiveCode(String lblNotReceiveCode) {
            this.lblNotReceiveCode = lblNotReceiveCode;
        }

        public String getLblOtpSentTo() {
            return lblOtpSentTo;
        }

        public void setLblOtpSentTo(String lblOtpSentTo) {
            this.lblOtpSentTo = lblOtpSentTo;
        }

        public String getLblPassword() {
            return lblPassword;
        }

        public void setLblPassword(String lblPassword) {
            this.lblPassword = lblPassword;
        }

        public String getLblPasswordSuccess() {
            return lblPasswordSuccess;
        }

        public void setLblPasswordSuccess(String lblPasswordSuccess) {
            this.lblPasswordSuccess = lblPasswordSuccess;
        }

        public String getLblPleaseWait() {
            return lblPleaseWait;
        }

        public void setLblPleaseWait(String lblPleaseWait) {
            this.lblPleaseWait = lblPleaseWait;
        }

        public String getLblPrivacy() {
            return lblPrivacy;
        }

        public void setLblPrivacy(String lblPrivacy) {
            this.lblPrivacy = lblPrivacy;
        }

        public String getLblRegister() {
            return lblRegister;
        }

        public void setLblRegister(String lblRegister) {
            this.lblRegister = lblRegister;
        }

        public String getLblResendCode() {
            return lblResendCode;
        }

        public void setLblResendCode(String lblResendCode) {
            this.lblResendCode = lblResendCode;
        }

        public String getLblResendOtp() {
            return lblResendOtp;
        }

        public void setLblResendOtp(String lblResendOtp) {
            this.lblResendOtp = lblResendOtp;
        }

        public String getLblSelectCountry() {
            return lblSelectCountry;
        }

        public void setLblSelectCountry(String lblSelectCountry) {
            this.lblSelectCountry = lblSelectCountry;
        }

        public String getLblSendingOtp() {
            return lblSendingOtp;
        }

        public void setLblSendingOtp(String lblSendingOtp) {
            this.lblSendingOtp = lblSendingOtp;
        }

        public String getLblSentTo() {
            return lblSentTo;
        }

        public void setLblSentTo(String lblSentTo) {
            this.lblSentTo = lblSentTo;
        }

        public String getLblUsername() {
            return lblUsername;
        }

        public void setLblUsername(String lblUsername) {
            this.lblUsername = lblUsername;
        }

        public String getLblVerifyInprogress() {
            return lblVerifyInprogress;
        }

        public void setLblVerifyInprogress(String lblVerifyInprogress) {
            this.lblVerifyInprogress = lblVerifyInprogress;
        }

        public String getLblVerifyingOtp() {
            return lblVerifyingOtp;
        }

        public void setLblVerifyingOtp(String lblVerifyingOtp) {
            this.lblVerifyingOtp = lblVerifyingOtp;
        }

        public String getLblWrongMsg() {
            return lblWrongMsg;
        }

        public void setLblWrongMsg(String lblWrongMsg) {
            this.lblWrongMsg = lblWrongMsg;
        }

        public String getLblYes() {
            return lblYes;
        }

        public void setLblYes(String lblYes) {
            this.lblYes = lblYes;
        }

        public String getLblYourMobNo() {
            return lblYourMobNo;
        }

        public void setLblYourMobNo(String lblYourMobNo) {
            this.lblYourMobNo = lblYourMobNo;
        }

        public String getLblDefaultStatus() {
            return lblDefaultStatus;
        }

        public void setLblDefaultStatus(String lblDefaultStatus) {
            this.lblDefaultStatus = lblDefaultStatus;
        }
    }

    public static class Homepage {
        public Homepage() {
        }

        @SerializedName("lbl_menu_calls")
        @Expose
        private String lblMenuCalls;
        @SerializedName("lbl_menu_chat")
        @Expose
        private String lblMenuChat;
        @SerializedName("lbl_menu_groups")
        @Expose
        private String lblMenuGroups;
        @SerializedName("lbl_menu_status")
        @Expose
        private String lblMenuStatus;
        @SerializedName("msg_restore")
        @Expose
        private String msgRestore;
        @SerializedName("lbl_favourite")
        @Expose
        private String lblFavourite;

        public String getLblFavourite() {
            return lblFavourite;
        }

        public void setLblFavourite(String lblFavourite) {
            this.lblFavourite = lblFavourite;
        }

        public String getLblMenuCalls() {
            return lblMenuCalls;
        }

        public void setLblMenuCalls(String lblMenuCalls) {
            this.lblMenuCalls = lblMenuCalls;
        }

        public String getLblMenuChat() {
            return lblMenuChat;
        }

        public void setLblMenuChat(String lblMenuChat) {
            this.lblMenuChat = lblMenuChat;
        }

        public String getLblMenuGroups() {
            return lblMenuGroups;
        }

        public void setLblMenuGroups(String lblMenuGroups) {
            this.lblMenuGroups = lblMenuGroups;
        }

        public String getLblMenuStatus() {
            return lblMenuStatus;
        }

        public void setLblMenuStatus(String lblMenuStatus) {
            this.lblMenuStatus = lblMenuStatus;
        }

        public String getMsgRestore() {
            return msgRestore;
        }

        public void setMsgRestore(String msgRestore) {
            this.msgRestore = msgRestore;
        }
    }

    public static class Chatpage {
        public Chatpage() {
        }

        @SerializedName("err_uploading_size")
        @Expose
        private String errUploadingSize;
        @SerializedName("lbl_attch_audio")
        @Expose
        private String lblAttchAudio;
        @SerializedName("lbl_attch_contact")
        @Expose
        private String lblAttchContact;
        @SerializedName("lbl_attch_document")
        @Expose
        private String lblAttchDocument;
        @SerializedName("lbl_attch_img")
        @Expose
        private String lblAttchImg;
        @SerializedName("lbl_attch_location")
        @Expose
        private String lblAttchLocation;
        @SerializedName("lbl_attch_recording")
        @Expose
        private String lblAttchRecording;
        @SerializedName("lbl_attch_recording_instruction")
        @Expose
        private String lblAttchRecordingInstruction;
        @SerializedName("lbl_attch_uploading")
        @Expose
        private String lblAttchUploading;
        @SerializedName("lbl_attch_video")
        @Expose
        private String lblAttchVideo;
        @SerializedName("lbl_chat_home")
        @Expose
        private String lblChatHome;
        @SerializedName("lbl_confirm_chat")
        @Expose
        private String lblConfirmChat;
        @SerializedName("lbl_delete_chat")
        @Expose
        private String lblDeleteChat;
        @SerializedName("lbl_delete_msg")
        @Expose
        private String lblDeleteMsg;
        @SerializedName("lbl_empty_chat")
        @Expose
        private String lblEmptyChat;
        @SerializedName("lbl_msg_hint")
        @Expose
        private String lblMsgHint;
        @SerializedName("lbl_delete_msg_title")
        @Expose
        private String lblDeleteMsgTitle;
        @SerializedName("lbl_last_seen")
        @Expose
        private String lblLastSeen;
        @SerializedName("lbl_online")
        @Expose
        private String lblOnline;
        @SerializedName("lbl_fwd_msg")
        @Expose
        private String lblFwdMsg;
        @SerializedName("lbl_group_admin")
        @Expose
        private String lblGroupAdmin;
        @SerializedName("lbl_created_on")
        @Expose
        private String lblCreatedOn;
        @SerializedName("lbl_fwd_to")
        @Expose
        private String lblFwdTo;
        @SerializedName("err_no_favorite")
        @Expose
        private String errNoFavorite;
        @SerializedName("lbl_user_not_found")
        @Expose
        private String lbl_user_not_found;
        @SerializedName("lbl_admin_blocked_you")
        @Expose
        private String lbl_admin_blocked_you;
        @SerializedName("lbl_report")
        @Expose
        private String lbl_report;
        @SerializedName("lbl_report_success")
        @Expose
        private String lbl_report_success;
        @SerializedName("lbl_user_not_available")
        @Expose
        private String lbl_user_not_available;
        @SerializedName("lbl_select_any_user")
        @Expose
        private String lbl_select_any_user;
        @SerializedName("lbl_max_user")
        @Expose
        private String lbl_max_user;
        @SerializedName("lbl_select_user_to_call")
        @Expose
        private String lbl_select_user_to_call;
        @SerializedName("lbl_make_call")
        @Expose
        private String lbl_make_call;
        @SerializedName("lbl_terms_validation")
        @Expose
        private String lbl_terms_validation;
        @SerializedName("lbl_select_contact")
        @Expose
        private String lbl_select_contact;
        @SerializedName("lbl_tab_to_unblock")
        @Expose
        private String lbl_tab_to_unblock;
        @SerializedName("lbl_tab_here_info")
        @Expose
        private String lbl_tab_here_info;

        public String getLbl_select_contact() {
            return lbl_select_contact;
        }

        public String getLbl_tab_to_unblock() {
            return lbl_tab_to_unblock;
        }

        public String getLbl_tab_here_info() {
            return lbl_tab_here_info;
        }

        public String getLbl_terms_validation() {
            return lbl_terms_validation;
        }

        public String getLbl_select_user_to_call() {
            return lbl_select_user_to_call;
        }

        public String getLbl_make_call() {
            return lbl_make_call;
        }

        public String getLbl_user_not_found() {
            return lbl_user_not_found;
        }

        public String getLbl_admin_blocked_you() {
            return lbl_admin_blocked_you;
        }

        public String getLbl_report() {
            return lbl_report;
        }

        public String getLbl_report_success() {
            return lbl_report_success;
        }

        public String getLbl_user_not_available() {
            return lbl_user_not_available;
        }

        public String getLbl_select_any_user() {
            return lbl_select_any_user;
        }

        public String getLbl_max_user() {
            return lbl_max_user;
        }

        public String getErrNoFavorite() {
            return errNoFavorite;
        }

        public String getLblFwdTo() {
            return lblFwdTo;
        }

        public void setLblFwdTo(String lblFwdTo) {
            this.lblFwdTo = lblFwdTo;
        }

        public String getLblLastSeen() {
            return lblLastSeen;
        }

        public void setLblLastSeen(String lblLastSeen) {
            this.lblLastSeen = lblLastSeen;
        }

        public String getLblOnline() {
            return lblOnline;
        }

        public void setLblOnline(String lblOnline) {
            this.lblOnline = lblOnline;
        }

        public String getLblFwdMsg() {
            return lblFwdMsg;
        }

        public void setLblFwdMsg(String lblFwdMsg) {
            this.lblFwdMsg = lblFwdMsg;
        }

        public String getLblGroupAdmin() {
            return lblGroupAdmin;
        }

        public void setLblGroupAdmin(String lblGroupAdmin) {
            this.lblGroupAdmin = lblGroupAdmin;
        }

        public String getLblCreatedOn() {
            return lblCreatedOn;
        }

        public void setLblCreatedOn(String lblCreatedOn) {
            this.lblCreatedOn = lblCreatedOn;
        }

        public String getErrUploadingSize() {
            return errUploadingSize;
        }

        public void setErrUploadingSize(String errUploadingSize) {
            this.errUploadingSize = errUploadingSize;
        }

        public String getLblAttchAudio() {
            return lblAttchAudio;
        }

        public void setLblAttchAudio(String lblAttchAudio) {
            this.lblAttchAudio = lblAttchAudio;
        }

        public String getLblAttchContact() {
            return lblAttchContact;
        }

        public void setLblAttchContact(String lblAttchContact) {
            this.lblAttchContact = lblAttchContact;
        }

        public String getLblAttchDocument() {
            return lblAttchDocument;
        }

        public void setLblAttchDocument(String lblAttchDocument) {
            this.lblAttchDocument = lblAttchDocument;
        }

        public String getLblAttchImg() {
            return lblAttchImg;
        }

        public void setLblAttchImg(String lblAttchImg) {
            this.lblAttchImg = lblAttchImg;
        }

        public String getLblAttchLocation() {
            return lblAttchLocation;
        }

        public void setLblAttchLocation(String lblAttchLocation) {
            this.lblAttchLocation = lblAttchLocation;
        }

        public String getLblAttchRecording() {
            return lblAttchRecording;
        }

        public void setLblAttchRecording(String lblAttchRecording) {
            this.lblAttchRecording = lblAttchRecording;
        }

        public String getLblAttchRecordingInstruction() {
            return lblAttchRecordingInstruction;
        }

        public void setLblAttchRecordingInstruction(String lblAttchRecordingInstruction) {
            this.lblAttchRecordingInstruction = lblAttchRecordingInstruction;
        }

        public String getLblAttchUploading() {
            return lblAttchUploading;
        }

        public void setLblAttchUploading(String lblAttchUploading) {
            this.lblAttchUploading = lblAttchUploading;
        }

        public String getLblAttchVideo() {
            return lblAttchVideo;
        }

        public void setLblAttchVideo(String lblAttchVideo) {
            this.lblAttchVideo = lblAttchVideo;
        }

        public String getLblChatHome() {
            return lblChatHome;
        }

        public void setLblChatHome(String lblChatHome) {
            this.lblChatHome = lblChatHome;
        }

        public String getLblConfirmChat() {
            return lblConfirmChat;
        }

        public void setLblConfirmChat(String lblConfirmChat) {
            this.lblConfirmChat = lblConfirmChat;
        }

        public String getLblDeleteChat() {
            return lblDeleteChat;
        }

        public void setLblDeleteChat(String lblDeleteChat) {
            this.lblDeleteChat = lblDeleteChat;
        }

        public String getLblDeleteMsg() {
            return lblDeleteMsg;
        }

        public void setLblDeleteMsg(String lblDeleteMsg) {
            this.lblDeleteMsg = lblDeleteMsg;
        }

        public String getLblEmptyChat() {
            return lblEmptyChat;
        }

        public void setLblEmptyChat(String lblEmptyChat) {
            this.lblEmptyChat = lblEmptyChat;
        }

        public String getLblMsgHint() {
            return lblMsgHint;
        }

        public void setLblMsgHint(String lblMsgHint) {
            this.lblMsgHint = lblMsgHint;
        }

        public String getLblDeleteMsgTitle() {
            return lblDeleteMsgTitle;
        }

        public void setLblDeleteMsgTitle(String lblDeleteMsgTitle) {
            this.lblDeleteMsgTitle = lblDeleteMsgTitle;
        }

    }

    public static class Grouppage {
        public Grouppage() {
        }

        @SerializedName("lbl_attach")
        @Expose
        private String lblAttach;
        @SerializedName("lbl_contact_info")
        @Expose
        private String lblContactInfo;
        @SerializedName("lbl_delete_group")
        @Expose
        private String lblDeleteGroup;
        @SerializedName("lbl_delete_group_msg")
        @Expose
        private String lblDeleteGroupMsg;
        @SerializedName("lbl_delete_success")
        @Expose
        private String lblDeleteSuccess;
        @SerializedName("lbl_document")
        @Expose
        private String lblDocument;
        @SerializedName("lbl_empty_group")
        @Expose
        private String lblEmptyGroup;
        @SerializedName("lbl_exit_group")
        @Expose
        private String lblExitGroup;
        @SerializedName("lbl_exit_group_msg")
        @Expose
        private String lblExitGroupMsg;
        @SerializedName("lbl_exit_success")
        @Expose
        private String lblExitSuccess;
        @SerializedName("lbl_group_home")
        @Expose
        private String lblGroupHome;
        @SerializedName("lbl_group_info")
        @Expose
        private String lblGroupInfo;
        @SerializedName("lbl_location")
        @Expose
        private String lblLocation;
        @SerializedName("lbl_photo_video")
        @Expose
        private String lblPhotoVideo;
        @SerializedName("lbl_remove_grp")
        @Expose
        private String lblRemoveGrp;
        @SerializedName("lbl_removed_info")
        @Expose
        private String lblRemovedInfo;

        public String getLblAttach() {
            return lblAttach;
        }

        public void setLblAttach(String lblAttach) {
            this.lblAttach = lblAttach;
        }

        public String getLblContactInfo() {
            return lblContactInfo;
        }

        public void setLblContactInfo(String lblContactInfo) {
            this.lblContactInfo = lblContactInfo;
        }

        public String getLblDeleteGroup() {
            return lblDeleteGroup;
        }

        public void setLblDeleteGroup(String lblDeleteGroup) {
            this.lblDeleteGroup = lblDeleteGroup;
        }

        public String getLblDeleteGroupMsg() {
            return lblDeleteGroupMsg;
        }

        public void setLblDeleteGroupMsg(String lblDeleteGroupMsg) {
            this.lblDeleteGroupMsg = lblDeleteGroupMsg;
        }

        public String getLblDeleteSuccess() {
            return lblDeleteSuccess;
        }

        public void setLblDeleteSuccess(String lblDeleteSuccess) {
            this.lblDeleteSuccess = lblDeleteSuccess;
        }

        public String getLblDocument() {
            return lblDocument;
        }

        public void setLblDocument(String lblDocument) {
            this.lblDocument = lblDocument;
        }

        public String getLblEmptyGroup() {
            return lblEmptyGroup;
        }

        public void setLblEmptyGroup(String lblEmptyGroup) {
            this.lblEmptyGroup = lblEmptyGroup;
        }

        public String getLblExitGroup() {
            return lblExitGroup;
        }

        public void setLblExitGroup(String lblExitGroup) {
            this.lblExitGroup = lblExitGroup;
        }

        public String getLblExitGroupMsg() {
            return lblExitGroupMsg;
        }

        public void setLblExitGroupMsg(String lblExitGroupMsg) {
            this.lblExitGroupMsg = lblExitGroupMsg;
        }

        public String getLblExitSuccess() {
            return lblExitSuccess;
        }

        public void setLblExitSuccess(String lblExitSuccess) {
            this.lblExitSuccess = lblExitSuccess;
        }

        public String getLblGroupHome() {
            return lblGroupHome;
        }

        public void setLblGroupHome(String lblGroupHome) {
            this.lblGroupHome = lblGroupHome;
        }

        public String getLblGroupInfo() {
            return lblGroupInfo;
        }

        public void setLblGroupInfo(String lblGroupInfo) {
            this.lblGroupInfo = lblGroupInfo;
        }

        public String getLblLocation() {
            return lblLocation;
        }

        public void setLblLocation(String lblLocation) {
            this.lblLocation = lblLocation;
        }

        public String getLblPhotoVideo() {
            return lblPhotoVideo;
        }

        public void setLblPhotoVideo(String lblPhotoVideo) {
            this.lblPhotoVideo = lblPhotoVideo;
        }

        public String getLblRemoveGrp() {
            return lblRemoveGrp;
        }

        public void setLblRemoveGrp(String lblRemoveGrp) {
            this.lblRemoveGrp = lblRemoveGrp;
        }

        public String getLblRemovedInfo() {
            return lblRemovedInfo;
        }

        public void setLblRemovedInfo(String lblRemovedInfo) {
            this.lblRemovedInfo = lblRemovedInfo;
        }
    }

    public static class Profiledetail {
        public Profiledetail() {
        }

        @SerializedName("err_empty_name")
        @Expose
        private String errEmptyName;
        @SerializedName("err_empty_status")
        @Expose
        private String errEmptyStatus;
        @SerializedName("err_unable_to_upload")
        @Expose
        private String errUnableToUpload;
        @SerializedName("lbl_add_member")
        @Expose
        private String lblAddMember;
        @SerializedName("lbl_block")
        @Expose
        private String lblBlock;
        @SerializedName("lbl_block_error_msg")
        @Expose
        private String lblBlockErrorMsg;
        @SerializedName("lbl_blocked")
        @Expose
        private String lblBlocked;
        @SerializedName("lbl_camera")
        @Expose
        private String lblCamera;
        @SerializedName("lbl_from_this_group")
        @Expose
        private String lblFromThisGroup;
        @SerializedName("lbl_gallery")
        @Expose
        private String lblGallery;
        @SerializedName("lbl_get_img_from")
        @Expose
        private String lblGetImgFrom;
        @SerializedName("lbl_group_admin_can_change")
        @Expose
        private String lblGroupAdminCanChange;
        @SerializedName("lbl_in_this_group")
        @Expose
        private String lblInThisGroup;
        @SerializedName("lbl_member_removed")
        @Expose
        private String lblMemberRemoved;
        @SerializedName("lbl_members")
        @Expose
        private String lblMembers;
        @SerializedName("lbl_mute_notification")
        @Expose
        private String lblMuteNotification;
        @SerializedName("lbl_no_new_member_to_add")
        @Expose
        private String lblNoNewMemberToAdd;
        @SerializedName("lbl_participants")
        @Expose
        private String lblParticipants;
        @SerializedName("lbl_phone_no")
        @Expose
        private String lblPhoneNo;
        @SerializedName("lbl_status")
        @Expose
        private String lblStatus;
        @SerializedName("lbl_unable_to_block")
        @Expose
        private String lblUnableToBlock;
        @SerializedName("lbl_unable_to_unblock")
        @Expose
        private String lblUnableToUnblock;
        @SerializedName("lbl_unblock")
        @Expose
        private String lblUnblock;
        @SerializedName("lbl_unblocked")
        @Expose
        private String lblUnblocked;
        @SerializedName("lbl_updated")
        @Expose
        private String lblUpdated;
        @SerializedName("lbl_want_to_add")
        @Expose
        private String lblWantToAdd;
        @SerializedName("lbl_want_to_remove")
        @Expose
        private String lblWantToRemove;
        @SerializedName("lbl_want_to_unblock")
        @Expose
        private String lblWantToUnblock;
        @SerializedName("lbl_you_added_group")
        @Expose
        private String lblYouAddedGroup;

        public String getErrEmptyName() {
            return errEmptyName;
        }

        public void setErrEmptyName(String errEmptyName) {
            this.errEmptyName = errEmptyName;
        }

        public String getErrEmptyStatus() {
            return errEmptyStatus;
        }

        public void setErrEmptyStatus(String errEmptyStatus) {
            this.errEmptyStatus = errEmptyStatus;
        }

        public String getErrUnableToUpload() {
            return errUnableToUpload;
        }

        public void setErrUnableToUpload(String errUnableToUpload) {
            this.errUnableToUpload = errUnableToUpload;
        }

        public String getLblAddMember() {
            return lblAddMember;
        }

        public void setLblAddMember(String lblAddMember) {
            this.lblAddMember = lblAddMember;
        }

        public String getLblBlock() {
            return lblBlock;
        }

        public void setLblBlock(String lblBlock) {
            this.lblBlock = lblBlock;
        }

        public String getLblBlockErrorMsg() {
            return lblBlockErrorMsg;
        }

        public void setLblBlockErrorMsg(String lblBlockErrorMsg) {
            this.lblBlockErrorMsg = lblBlockErrorMsg;
        }

        public String getLblBlocked() {
            return lblBlocked;
        }

        public void setLblBlocked(String lblBlocked) {
            this.lblBlocked = lblBlocked;
        }

        public String getLblCamera() {
            return lblCamera;
        }

        public void setLblCamera(String lblCamera) {
            this.lblCamera = lblCamera;
        }

        public String getLblFromThisGroup() {
            return lblFromThisGroup;
        }

        public void setLblFromThisGroup(String lblFromThisGroup) {
            this.lblFromThisGroup = lblFromThisGroup;
        }

        public String getLblGallery() {
            return lblGallery;
        }

        public void setLblGallery(String lblGallery) {
            this.lblGallery = lblGallery;
        }

        public String getLblGetImgFrom() {
            return lblGetImgFrom;
        }

        public void setLblGetImgFrom(String lblGetImgFrom) {
            this.lblGetImgFrom = lblGetImgFrom;
        }

        public String getLblGroupAdminCanChange() {
            return lblGroupAdminCanChange;
        }

        public void setLblGroupAdminCanChange(String lblGroupAdminCanChange) {
            this.lblGroupAdminCanChange = lblGroupAdminCanChange;
        }

        public String getLblInThisGroup() {
            return lblInThisGroup;
        }

        public void setLblInThisGroup(String lblInThisGroup) {
            this.lblInThisGroup = lblInThisGroup;
        }

        public String getLblMemberRemoved() {
            return lblMemberRemoved;
        }

        public void setLblMemberRemoved(String lblMemberRemoved) {
            this.lblMemberRemoved = lblMemberRemoved;
        }

        public String getLblMembers() {
            return lblMembers;
        }

        public void setLblMembers(String lblMembers) {
            this.lblMembers = lblMembers;
        }

        public String getLblMuteNotification() {
            return lblMuteNotification;
        }

        public void setLblMuteNotification(String lblMuteNotification) {
            this.lblMuteNotification = lblMuteNotification;
        }

        public String getLblNoNewMemberToAdd() {
            return lblNoNewMemberToAdd;
        }

        public void setLblNoNewMemberToAdd(String lblNoNewMemberToAdd) {
            this.lblNoNewMemberToAdd = lblNoNewMemberToAdd;
        }

        public String getLblParticipants() {
            return lblParticipants;
        }

        public void setLblParticipants(String lblParticipants) {
            this.lblParticipants = lblParticipants;
        }

        public String getLblPhoneNo() {
            return lblPhoneNo;
        }

        public void setLblPhoneNo(String lblPhoneNo) {
            this.lblPhoneNo = lblPhoneNo;
        }

        public String getLblStatus() {
            return lblStatus;
        }

        public void setLblStatus(String lblStatus) {
            this.lblStatus = lblStatus;
        }

        public String getLblUnableToBlock() {
            return lblUnableToBlock;
        }

        public void setLblUnableToBlock(String lblUnableToBlock) {
            this.lblUnableToBlock = lblUnableToBlock;
        }

        public String getLblUnableToUnblock() {
            return lblUnableToUnblock;
        }

        public void setLblUnableToUnblock(String lblUnableToUnblock) {
            this.lblUnableToUnblock = lblUnableToUnblock;
        }

        public String getLblUnblock() {
            return lblUnblock;
        }

        public void setLblUnblock(String lblUnblock) {
            this.lblUnblock = lblUnblock;
        }

        public String getLblUnblocked() {
            return lblUnblocked;
        }

        public void setLblUnblocked(String lblUnblocked) {
            this.lblUnblocked = lblUnblocked;
        }

        public String getLblUpdated() {
            return lblUpdated;
        }

        public void setLblUpdated(String lblUpdated) {
            this.lblUpdated = lblUpdated;
        }

        public String getLblWantToAdd() {
            return lblWantToAdd;
        }

        public void setLblWantToAdd(String lblWantToAdd) {
            this.lblWantToAdd = lblWantToAdd;
        }

        public String getLblWantToRemove() {
            return lblWantToRemove;
        }

        public void setLblWantToRemove(String lblWantToRemove) {
            this.lblWantToRemove = lblWantToRemove;
        }

        public String getLblWantToUnblock() {
            return lblWantToUnblock;
        }

        public void setLblWantToUnblock(String lblWantToUnblock) {
            this.lblWantToUnblock = lblWantToUnblock;
        }

        public String getLblYouAddedGroup() {
            return lblYouAddedGroup;
        }

        public void setLblYouAddedGroup(String lblYouAddedGroup) {
            this.lblYouAddedGroup = lblYouAddedGroup;
        }
    }

    public static class Creategrouppage {
        public Creategrouppage() {
        }

        @SerializedName("err_group_desc_empty")
        @Expose
        private String errGroupDescEmpty;
        @SerializedName("err_group_name_empty")
        @Expose
        private String errGroupNameEmpty;
        @SerializedName("lbl_add_more")
        @Expose
        private String lblAddMore;
        @SerializedName("lbl_camera")
        @Expose
        private String lblCamera;
        @SerializedName("lbl_cancel")
        @Expose
        private String lblCancel;
        @SerializedName("lbl_create_group")
        @Expose
        private String lblCreateGroup;
        @SerializedName("lbl_done")
        @Expose
        private String lblDone;
        @SerializedName("lbl_group_desc")
        @Expose
        private String lblGroupDesc;
        @SerializedName("lbl_group_img_uploading")
        @Expose
        private String lblGroupImgUploading;
        @SerializedName("lbl_group_info")
        @Expose
        private String lblGroupInfo;
        @SerializedName("lbl_group_name")
        @Expose
        private String lblGroupName;
        @SerializedName("lbl_no_friends")
        @Expose
        private String lblNoFriends;
        @SerializedName("lbl_no_participants")
        @Expose
        private String lblNoParticipants;
        @SerializedName("lbl_participants")
        @Expose
        private String lblParticipants;
        @SerializedName("lbl_photo")
        @Expose
        private String lblPhoto;
        @SerializedName("lbl_photo_for_group")
        @Expose
        private String lblPhotoForGroup;

        public String getErrGroupDescEmpty() {
            return errGroupDescEmpty;
        }

        public void setErrGroupDescEmpty(String errGroupDescEmpty) {
            this.errGroupDescEmpty = errGroupDescEmpty;
        }

        public String getErrGroupNameEmpty() {
            return errGroupNameEmpty;
        }

        public void setErrGroupNameEmpty(String errGroupNameEmpty) {
            this.errGroupNameEmpty = errGroupNameEmpty;
        }

        public String getLblAddMore() {
            return lblAddMore;
        }

        public void setLblAddMore(String lblAddMore) {
            this.lblAddMore = lblAddMore;
        }

        public String getLblCamera() {
            return lblCamera;
        }

        public void setLblCamera(String lblCamera) {
            this.lblCamera = lblCamera;
        }

        public String getLblCancel() {
            return lblCancel;
        }

        public void setLblCancel(String lblCancel) {
            this.lblCancel = lblCancel;
        }

        public String getLblCreateGroup() {
            return lblCreateGroup;
        }

        public void setLblCreateGroup(String lblCreateGroup) {
            this.lblCreateGroup = lblCreateGroup;
        }

        public String getLblDone() {
            return lblDone;
        }

        public void setLblDone(String lblDone) {
            this.lblDone = lblDone;
        }

        public String getLblGroupDesc() {
            return lblGroupDesc;
        }

        public void setLblGroupDesc(String lblGroupDesc) {
            this.lblGroupDesc = lblGroupDesc;
        }

        public String getLblGroupImgUploading() {
            return lblGroupImgUploading;
        }

        public void setLblGroupImgUploading(String lblGroupImgUploading) {
            this.lblGroupImgUploading = lblGroupImgUploading;
        }

        public String getLblGroupInfo() {
            return lblGroupInfo;
        }

        public void setLblGroupInfo(String lblGroupInfo) {
            this.lblGroupInfo = lblGroupInfo;
        }

        public String getLblGroupName() {
            return lblGroupName;
        }

        public void setLblGroupName(String lblGroupName) {
            this.lblGroupName = lblGroupName;
        }

        public String getLblNoFriends() {
            return lblNoFriends;
        }

        public void setLblNoFriends(String lblNoFriends) {
            this.lblNoFriends = lblNoFriends;
        }

        public String getLblNoParticipants() {
            return lblNoParticipants;
        }

        public void setLblNoParticipants(String lblNoParticipants) {
            this.lblNoParticipants = lblNoParticipants;
        }

        public String getLblParticipants() {
            return lblParticipants;
        }

        public void setLblParticipants(String lblParticipants) {
            this.lblParticipants = lblParticipants;
        }

        public String getLblPhoto() {
            return lblPhoto;
        }

        public void setLblPhoto(String lblPhoto) {
            this.lblPhoto = lblPhoto;
        }

        public String getLblPhotoForGroup() {
            return lblPhotoForGroup;
        }

        public void setLblPhotoForGroup(String lblPhotoForGroup) {
            this.lblPhotoForGroup = lblPhotoForGroup;
        }
    }

    public static class Statuspage {
        public Statuspage() {
        }

        @SerializedName("lbl_choose_file")
        @Expose
        private String lblChooseFile;
        @SerializedName("lbl_drag")
        @Expose
        private String lblDrag;
        @SerializedName("lbl_drag_drop")
        @Expose
        private String lblDragDrop;
        @SerializedName("lbl_home_msg")
        @Expose
        private String lblHomeMsg;
        @SerializedName("lbl_my_status")
        @Expose
        private String lblMyStatus;
        @SerializedName("lbl_new_status")
        @Expose
        private String lblNewStatus;
        @SerializedName("lbl_recent")
        @Expose
        private String lblRecent;
        @SerializedName("lbl_status")
        @Expose
        private String lblStatus;
        @SerializedName("lbl_status_empty")
        @Expose
        private String lblStatusEmpty;
        @SerializedName("lbl_tab_to_add_status")
        @Expose
        private String lblTabToAddStatus;
        @SerializedName("lbl_upload_max")
        @Expose
        private String lblUploadMax;
        @SerializedName("lbl_uploading")
        @Expose
        private String lblUploading;
        @SerializedName("lbl_delete_confirmation")
        @Expose
        private String lblDeleteConfirmation;
        @SerializedName("lbl_delete_status")
        @Expose
        private String lblDeleteStatus;

        public String getLblDeleteStatus() {
            return lblDeleteStatus;
        }

        public void setLblDeleteStatus(String lblDeleteStatus) {
            this.lblDeleteStatus = lblDeleteStatus;
        }

        public String getLblChooseFile() {
            return lblChooseFile;
        }

        public void setLblChooseFile(String lblChooseFile) {
            this.lblChooseFile = lblChooseFile;
        }

        public String getLblDrag() {
            return lblDrag;
        }

        public void setLblDrag(String lblDrag) {
            this.lblDrag = lblDrag;
        }

        public String getLblDragDrop() {
            return lblDragDrop;
        }

        public void setLblDragDrop(String lblDragDrop) {
            this.lblDragDrop = lblDragDrop;
        }

        public String getLblHomeMsg() {
            return lblHomeMsg;
        }

        public void setLblHomeMsg(String lblHomeMsg) {
            this.lblHomeMsg = lblHomeMsg;
        }

        public String getLblMyStatus() {
            return lblMyStatus;
        }

        public void setLblMyStatus(String lblMyStatus) {
            this.lblMyStatus = lblMyStatus;
        }

        public String getLblNewStatus() {
            return lblNewStatus;
        }

        public void setLblNewStatus(String lblNewStatus) {
            this.lblNewStatus = lblNewStatus;
        }

        public String getLblRecent() {
            return lblRecent;
        }

        public void setLblRecent(String lblRecent) {
            this.lblRecent = lblRecent;
        }

        public String getLblStatus() {
            return lblStatus;
        }

        public void setLblStatus(String lblStatus) {
            this.lblStatus = lblStatus;
        }

        public String getLblStatusEmpty() {
            return lblStatusEmpty;
        }

        public void setLblStatusEmpty(String lblStatusEmpty) {
            this.lblStatusEmpty = lblStatusEmpty;
        }

        public String getLblTabToAddStatus() {
            return lblTabToAddStatus;
        }

        public void setLblTabToAddStatus(String lblTabToAddStatus) {
            this.lblTabToAddStatus = lblTabToAddStatus;
        }

        public String getLblUploadMax() {
            return lblUploadMax;
        }

        public void setLblUploadMax(String lblUploadMax) {
            this.lblUploadMax = lblUploadMax;
        }

        public String getLblUploading() {
            return lblUploading;
        }

        public void setLblUploading(String lblUploading) {
            this.lblUploading = lblUploading;
        }

        public String getLblDeleteConfirmation() {
            return lblDeleteConfirmation;
        }

        public void setLblDeleteConfirmation(String lblDeleteConfirmation) {
            this.lblDeleteConfirmation = lblDeleteConfirmation;
        }
    }

    public static class Callspage {
        public Callspage() {
        }

        @SerializedName("lbl_calls_empty")
        @Expose
        private String lblCallsEmpty;
        @SerializedName("lbl_home_msg")
        @Expose
        private String lblHomeMsg;
        @SerializedName("lbl_missed_call")
        @Expose
        private String lblMissedCall;
        @SerializedName("lbl_other_call")
        @Expose
        private String lblOtherCall;
        @SerializedName("lbl_search_title")
        @Expose
        private String lblSearchTitle;
        @SerializedName("lbl_create_new_call")
        @Expose
        private String lblCreateNewCall;
        @SerializedName("lbl_enter_username_search")
        @Expose
        private String lblEnterUsernameSearch;
        @SerializedName("lbl_voice_call")
        @Expose
        private String lblVoiceCall;
        @SerializedName("lbl_video_call")
        @Expose
        private String lblVideoCall;
        @SerializedName("lbl_delete_log")
        @Expose
        private String lblDeleteLog;
        @SerializedName("lbl_delete_message")
        @Expose
        private String lblDeleteMessage;

        public String getLblDeleteLog() {
            return lblDeleteLog;
        }

        public void setLblDeleteLog(String lblDeleteLog) {
            this.lblDeleteLog = lblDeleteLog;
        }

        public String getLblDeleteMessage() {
            return lblDeleteMessage;
        }

        public void setLblDeleteMessage(String lblDeleteMessage) {
            this.lblDeleteMessage = lblDeleteMessage;
        }

        public String getLblCallsEmpty() {
            return lblCallsEmpty;
        }

        public void setLblCallsEmpty(String lblCallsEmpty) {
            this.lblCallsEmpty = lblCallsEmpty;
        }

        public String getLblHomeMsg() {
            return lblHomeMsg;
        }

        public void setLblHomeMsg(String lblHomeMsg) {
            this.lblHomeMsg = lblHomeMsg;
        }

        public String getLblMissedCall() {
            return lblMissedCall;
        }

        public void setLblMissedCall(String lblMissedCall) {
            this.lblMissedCall = lblMissedCall;
        }

        public String getLblOtherCall() {
            return lblOtherCall;
        }

        public void setLblOtherCall(String lblOtherCall) {
            this.lblOtherCall = lblOtherCall;
        }

        public String getLblSearchTitle() {
            return lblSearchTitle;
        }

        public void setLblSearchTitle(String lblSearchTitle) {
            this.lblSearchTitle = lblSearchTitle;
        }

        public String getLblCreateNewCall() {
            return lblCreateNewCall;
        }

        public void setLblCreateNewCall(String lblCreateNewCall) {
            this.lblCreateNewCall = lblCreateNewCall;
        }

        public String getLblEnterUsernameSearch() {
            return lblEnterUsernameSearch;
        }

        public void setLblEnterUsernameSearch(String lblEnterUsernameSearch) {
            this.lblEnterUsernameSearch = lblEnterUsernameSearch;
        }

        public String getLblVoiceCall() {
            return lblVoiceCall;
        }

        public void setLblVoiceCall(String lblVoiceCall) {
            this.lblVoiceCall = lblVoiceCall;
        }

        public String getLblVideoCall() {
            return lblVideoCall;
        }

        public void setLblVideoCall(String lblVideoCall) {
            this.lblVideoCall = lblVideoCall;
        }
    }

    public static class Settingspage {
        public Settingspage() {
        }

        @SerializedName("err_name")
        @Expose
        private String errName;
        @SerializedName("err_status")
        @Expose
        private String errStatus;
        @SerializedName("lbl_about")
        @Expose
        private String lblAbout;
        @SerializedName("lbl_change_language")
        @Expose
        private String lblChangeLanguage;
        @SerializedName("lbl_change_password")
        @Expose
        private String lblChangePassword;
        @SerializedName("lbl_contact_us")
        @Expose
        private String lblContactUs;
        @SerializedName("lbl_enter_name")
        @Expose
        private String lblEnterName;
        @SerializedName("lbl_enter_profile_name")
        @Expose
        private String lblEnterProfileName;
        @SerializedName("lbl_enter_status")
        @Expose
        private String lblEnterStatus;
        @SerializedName("lbl_invite")
        @Expose
        private String lblInvite;
        @SerializedName("lbl_logout")
        @Expose
        private String lblLogout;
        @SerializedName("lbl_phone")
        @Expose
        private String lblPhone;
        @SerializedName("lbl_privacy")
        @Expose
        private String lblPrivacy;
        @SerializedName("lbl_profile")
        @Expose
        private String lblProfile;
        @SerializedName("lbl_profile_img_updated")
        @Expose
        private String lblProfileImgUpdated;
        @SerializedName("lbl_profile_name")
        @Expose
        private String lblProfileName;
        @SerializedName("lbl_profile_photo")
        @Expose
        private String lblProfilePhoto;
        @SerializedName("lbl_profile_upload")
        @Expose
        private String lblProfileUpload;
        @SerializedName("lbl_rate_app")
        @Expose
        private String lblRateApp;
        @SerializedName("lbl_settings")
        @Expose
        private String lblSettings;
        @SerializedName("lbl_share_app")
        @Expose
        private String lblShareApp;
        @SerializedName("lbl_updated")
        @Expose
        private String lblUpdated;
        @SerializedName("lbl_wallpaper")
        @Expose
        private String lblWallpaper;
        @SerializedName("lbl_wallpaper_upload")
        @Expose
        private String lblWallpaperUpload;
        @SerializedName("lbl_want_to_logout")
        @Expose
        private String lblWantToLogout;
        @SerializedName("lbl_share_app_content")
        @Expose
        private String lblShareAppContent;
        @SerializedName("lbl_invite_content")
        @Expose
        private String lblInviteContent;
        @SerializedName("lbl_remove_wallpaper")
        @Expose
        private String lbl_remove_wallpaper;
        @SerializedName("lbl_near_by_users")
        @Expose
        private String lbl_near_by_users;
        @SerializedName("lbl_blocked_users")
        @Expose
        private String lbl_blocked_users;
        @SerializedName("lbl_no_blocked_user")
        @Expose
        private String lbl_no_blocked_user;
        @SerializedName("lbl_notification")
        @Expose
        private String lbl_notification;
        @SerializedName("lbl_notification_not_found")
        @Expose
        private String lbl_notification_not_found;

        public String getLbl_notification_not_found() {
            return lbl_notification_not_found;
        }

        public String getLbl_notification() {
            return lbl_notification;
        }

        public String getLbl_no_blocked_user() {
            return lbl_no_blocked_user;
        }

        public String getLbl_near_by_users() {
            return lbl_near_by_users;
        }

        public String getLbl_blocked_users() {
            return lbl_blocked_users;
        }

        public String getLbl_remove_wallpaper() {
            return lbl_remove_wallpaper;
        }

        public void setLbl_remove_wallpaper(String lbl_remove_wallpaper) {
            this.lbl_remove_wallpaper = lbl_remove_wallpaper;
        }

        public String getErrName() {
            return errName;
        }

        public void setErrName(String errName) {
            this.errName = errName;
        }

        public String getErrStatus() {
            return errStatus;
        }

        public void setErrStatus(String errStatus) {
            this.errStatus = errStatus;
        }

        public String getLblAbout() {
            return lblAbout;
        }

        public void setLblAbout(String lblAbout) {
            this.lblAbout = lblAbout;
        }

        public String getLblChangeLanguage() {
            return lblChangeLanguage;
        }

        public void setLblChangeLanguage(String lblChangeLanguage) {
            this.lblChangeLanguage = lblChangeLanguage;
        }

        public String getLblChangePassword() {
            return lblChangePassword;
        }

        public void setLblChangePassword(String lblChangePassword) {
            this.lblChangePassword = lblChangePassword;
        }

        public String getLblContactUs() {
            return lblContactUs;
        }

        public void setLblContactUs(String lblContactUs) {
            this.lblContactUs = lblContactUs;
        }

        public String getLblEnterName() {
            return lblEnterName;
        }

        public void setLblEnterName(String lblEnterName) {
            this.lblEnterName = lblEnterName;
        }

        public String getLblEnterProfileName() {
            return lblEnterProfileName;
        }

        public void setLblEnterProfileName(String lblEnterProfileName) {
            this.lblEnterProfileName = lblEnterProfileName;
        }

        public String getLblEnterStatus() {
            return lblEnterStatus;
        }

        public void setLblEnterStatus(String lblEnterStatus) {
            this.lblEnterStatus = lblEnterStatus;
        }

        public String getLblInvite() {
            return lblInvite;
        }

        public void setLblInvite(String lblInvite) {
            this.lblInvite = lblInvite;
        }

        public String getLblLogout() {
            return lblLogout;
        }

        public void setLblLogout(String lblLogout) {
            this.lblLogout = lblLogout;
        }

        public String getLblPhone() {
            return lblPhone;
        }

        public void setLblPhone(String lblPhone) {
            this.lblPhone = lblPhone;
        }

        public String getLblPrivacy() {
            return lblPrivacy;
        }

        public void setLblPrivacy(String lblPrivacy) {
            this.lblPrivacy = lblPrivacy;
        }

        public String getLblProfile() {
            return lblProfile;
        }

        public void setLblProfile(String lblProfile) {
            this.lblProfile = lblProfile;
        }

        public String getLblProfileImgUpdated() {
            return lblProfileImgUpdated;
        }

        public void setLblProfileImgUpdated(String lblProfileImgUpdated) {
            this.lblProfileImgUpdated = lblProfileImgUpdated;
        }

        public String getLblProfileName() {
            return lblProfileName;
        }

        public void setLblProfileName(String lblProfileName) {
            this.lblProfileName = lblProfileName;
        }

        public String getLblProfilePhoto() {
            return lblProfilePhoto;
        }

        public void setLblProfilePhoto(String lblProfilePhoto) {
            this.lblProfilePhoto = lblProfilePhoto;
        }

        public String getLblProfileUpload() {
            return lblProfileUpload;
        }

        public void setLblProfileUpload(String lblProfileUpload) {
            this.lblProfileUpload = lblProfileUpload;
        }

        public String getLblRateApp() {
            return lblRateApp;
        }

        public void setLblRateApp(String lblRateApp) {
            this.lblRateApp = lblRateApp;
        }

        public String getLblSettings() {
            return lblSettings;
        }

        public void setLblSettings(String lblSettings) {
            this.lblSettings = lblSettings;
        }

        public String getLblShareApp() {
            return lblShareApp;
        }

        public void setLblShareApp(String lblShareApp) {
            this.lblShareApp = lblShareApp;
        }

        public String getLblUpdated() {
            return lblUpdated;
        }

        public void setLblUpdated(String lblUpdated) {
            this.lblUpdated = lblUpdated;
        }

        public String getLblWallpaper() {
            return lblWallpaper;
        }

        public void setLblWallpaper(String lblWallpaper) {
            this.lblWallpaper = lblWallpaper;
        }

        public String getLblWallpaperUpload() {
            return lblWallpaperUpload;
        }

        public void setLblWallpaperUpload(String lblWallpaperUpload) {
            this.lblWallpaperUpload = lblWallpaperUpload;
        }

        public String getLblWantToLogout() {
            return lblWantToLogout;
        }

        public void setLblWantToLogout(String lblWantToLogout) {
            this.lblWantToLogout = lblWantToLogout;
        }

        public String getLblShareAppContent() {
            return lblShareAppContent;
        }

        public void setLblShareAppContent(String lblShareAppContent) {
            this.lblShareAppContent = lblShareAppContent;
        }

        public String getLblInviteContent() {
            return lblInviteContent;
        }

        public void setLblInviteContent(String lblInviteContent) {
            this.lblInviteContent = lblInviteContent;
        }
    }

    public class ChangePasswordPage {
        public ChangePasswordPage() {
        }

        @SerializedName("err_current_new_not_same")
        @Expose
        private String errCurrentNewNotSame;
        @SerializedName("err_current_not_correct")
        @Expose
        private String errCurrentNotCorrect;
        @SerializedName("err_new_confirm_same")
        @Expose
        private String errNewConfirmSame;
        @SerializedName("lbl_change_password")
        @Expose
        private String lblChangePassword;
        @SerializedName("lbl_enter_confirm_passowrd")
        @Expose
        private String lblEnterConfirmPassowrd;
        @SerializedName("lbl_enter_current_passowrd")
        @Expose
        private String lblEnterCurrentPassowrd;
        @SerializedName("lbl_enter_new_passowrd")
        @Expose
        private String lblEnterNewPassowrd;
        @SerializedName("lbl_pwd_success")
        @Expose
        private String lblPwdSuccess;
        @SerializedName("lbl_submit")
        @Expose
        private String lblSubmit;

        public String getErrCurrentNewNotSame() {
            return errCurrentNewNotSame;
        }

        public void setErrCurrentNewNotSame(String errCurrentNewNotSame) {
            this.errCurrentNewNotSame = errCurrentNewNotSame;
        }

        public String getErrCurrentNotCorrect() {
            return errCurrentNotCorrect;
        }

        public void setErrCurrentNotCorrect(String errCurrentNotCorrect) {
            this.errCurrentNotCorrect = errCurrentNotCorrect;
        }

        public String getErrNewConfirmSame() {
            return errNewConfirmSame;
        }

        public void setErrNewConfirmSame(String errNewConfirmSame) {
            this.errNewConfirmSame = errNewConfirmSame;
        }

        public String getLblChangePassword() {
            return lblChangePassword;
        }

        public void setLblChangePassword(String lblChangePassword) {
            this.lblChangePassword = lblChangePassword;
        }

        public String getLblEnterConfirmPassowrd() {
            return lblEnterConfirmPassowrd;
        }

        public void setLblEnterConfirmPassowrd(String lblEnterConfirmPassowrd) {
            this.lblEnterConfirmPassowrd = lblEnterConfirmPassowrd;
        }

        public String getLblEnterCurrentPassowrd() {
            return lblEnterCurrentPassowrd;
        }

        public void setLblEnterCurrentPassowrd(String lblEnterCurrentPassowrd) {
            this.lblEnterCurrentPassowrd = lblEnterCurrentPassowrd;
        }

        public String getLblEnterNewPassowrd() {
            return lblEnterNewPassowrd;
        }

        public void setLblEnterNewPassowrd(String lblEnterNewPassowrd) {
            this.lblEnterNewPassowrd = lblEnterNewPassowrd;
        }

        public String getLblPwdSuccess() {
            return lblPwdSuccess;
        }

        public void setLblPwdSuccess(String lblPwdSuccess) {
            this.lblPwdSuccess = lblPwdSuccess;
        }

        public String getLblSubmit() {
            return lblSubmit;
        }

        public void setLblSubmit(String lblSubmit) {
            this.lblSubmit = lblSubmit;
        }
    }

    public class Schedulepage {

        public Schedulepage() {
        }

        @SerializedName("lbl_schedule_delete")
        @Expose
        private String lblScheduleDelete;
        @SerializedName("lbl_schedule_new")
        @Expose
        private String lblScheduleNew;
        @SerializedName("lbl_schedule_to_delete")
        @Expose
        private String lblScheduleToDelete;
        @SerializedName("lbl_date_and_time")
        @Expose
        private String lblDateAndTime;
        @SerializedName("lbl_select_chats")
        @Expose
        private String lblSelectChats;
        @SerializedName("lbl_to_delete_selected_chat")
        @Expose
        private String lblToDeleteSelectedChat;
        @SerializedName("lbl_reschudle")
        @Expose
        private String lblReschudle;
        @SerializedName("lbl_select_all")
        @Expose
        private String lblSelectAll;
        @SerializedName("lbl_unselect_all")
        @Expose
        private String lblUnselectAll;
        @SerializedName("lbl_schedule")
        @Expose
        private String lblSchedule;
        @SerializedName("lbl_schedule_success")
        @Expose
        private String lblScheduleSuccess;
        @SerializedName("lbl_select_user_to_schedule")
        @Expose
        private String lbl_select_user_to_schedule;

        public String getLbl_select_user_to_schedule() {
            return lbl_select_user_to_schedule;
        }

        public String getLblScheduleDelete() {
            return lblScheduleDelete;
        }

        public void setLblScheduleDelete(String lblScheduleDelete) {
            this.lblScheduleDelete = lblScheduleDelete;
        }

        public String getLblScheduleNew() {
            return lblScheduleNew;
        }

        public void setLblScheduleNew(String lblScheduleNew) {
            this.lblScheduleNew = lblScheduleNew;
        }

        public String getLblScheduleToDelete() {
            return lblScheduleToDelete;
        }

        public void setLblScheduleToDelete(String lblScheduleToDelete) {
            this.lblScheduleToDelete = lblScheduleToDelete;
        }

        public String getLblDateAndTime() {
            return lblDateAndTime;
        }

        public void setLblDateAndTime(String lblDateAndTime) {
            this.lblDateAndTime = lblDateAndTime;
        }

        public String getLblSelectChats() {
            return lblSelectChats;
        }

        public void setLblSelectChats(String lblSelectChats) {
            this.lblSelectChats = lblSelectChats;
        }

        public String getLblToDeleteSelectedChat() {
            return lblToDeleteSelectedChat;
        }

        public void setLblToDeleteSelectedChat(String lblToDeleteSelectedChat) {
            this.lblToDeleteSelectedChat = lblToDeleteSelectedChat;
        }

        public String getLblReschudle() {
            return lblReschudle;
        }

        public void setLblReschudle(String lblReschudle) {
            this.lblReschudle = lblReschudle;
        }

        public String getLblSelectAll() {
            return lblSelectAll;
        }

        public void setLblSelectAll(String lblSelectAll) {
            this.lblSelectAll = lblSelectAll;
        }

        public String getLblUnselectAll() {
            return lblUnselectAll;
        }

        public void setLblUnselectAll(String lblUnselectAll) {
            this.lblUnselectAll = lblUnselectAll;
        }

        public String getLblSchedule() {
            return lblSchedule;
        }

        public void setLblSchedule(String lblSchedule) {
            this.lblSchedule = lblSchedule;
        }

        public String getLblScheduleSuccess() {
            return lblScheduleSuccess;
        }

        public void setLblScheduleSuccess(String lblScheduleSuccess) {
            this.lblScheduleSuccess = lblScheduleSuccess;
        }
    }

    public class Passcodepage {

        @SerializedName("lbl_app_lock")
        @Expose
        private String lblAppLock;
        @SerializedName("lbl_old_passcode")
        @Expose
        private String lblOldPasscode;
        @SerializedName("lbl_new_passcode")
        @Expose
        private String lblNewPasscode;
        @SerializedName("lbl_inccorect_old_passcode")
        @Expose
        private String lblInccorectOldPasscode;
        @SerializedName("lbl_confirm_passcode")
        @Expose
        private String lblConfirmPasscode;
        @SerializedName("lbl_passcode_success")
        @Expose
        private String lblPasscodeSuccess;
        @SerializedName("lbl_passcode_not_match")
        @Expose
        private String lblPasscodeNotMatch;
        @SerializedName("lbl_passcode")
        @Expose
        private String lblPasscode;
        @SerializedName("lbl_passcode_not_correct")
        @Expose
        private String lblPasscodeNotCorrect;

        public String getLblAppLock() {
            return lblAppLock;
        }

        public void setLblAppLock(String lblAppLock) {
            this.lblAppLock = lblAppLock;
        }

        public String getLblOldPasscode() {
            return lblOldPasscode;
        }

        public void setLblOldPasscode(String lblOldPasscode) {
            this.lblOldPasscode = lblOldPasscode;
        }

        public String getLblNewPasscode() {
            return lblNewPasscode;
        }

        public void setLblNewPasscode(String lblNewPasscode) {
            this.lblNewPasscode = lblNewPasscode;
        }

        public String getLblInccorectOldPasscode() {
            return lblInccorectOldPasscode;
        }

        public void setLblInccorectOldPasscode(String lblInccorectOldPasscode) {
            this.lblInccorectOldPasscode = lblInccorectOldPasscode;
        }

        public String getLblConfirmPasscode() {
            return lblConfirmPasscode;
        }

        public void setLblConfirmPasscode(String lblConfirmPasscode) {
            this.lblConfirmPasscode = lblConfirmPasscode;
        }

        public String getLblPasscodeSuccess() {
            return lblPasscodeSuccess;
        }

        public void setLblPasscodeSuccess(String lblPasscodeSuccess) {
            this.lblPasscodeSuccess = lblPasscodeSuccess;
        }

        public String getLblPasscodeNotMatch() {
            return lblPasscodeNotMatch;
        }

        public void setLblPasscodeNotMatch(String lblPasscodeNotMatch) {
            this.lblPasscodeNotMatch = lblPasscodeNotMatch;
        }

        public String getLblPasscode() {
            return lblPasscode;
        }

        public void setLblPasscode(String lblPasscode) {
            this.lblPasscode = lblPasscode;
        }

        public String getLblPasscodeNotCorrect() {
            return lblPasscodeNotCorrect;
        }

        public void setLblPasscodeNotCorrect(String lblPasscodeNotCorrect) {
            this.lblPasscodeNotCorrect = lblPasscodeNotCorrect;
        }

    }
}
