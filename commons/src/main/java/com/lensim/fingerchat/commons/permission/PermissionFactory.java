package com.lensim.fingerchat.commons.permission;


import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.helper.ContextHelper;

public class PermissionFactory extends PermissionFactoryBase {

    @Override
    protected PermissionStorage createPermissionStorage() {
        return new PermissionStorage() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask),
                            ContextHelper.getString(R.string.storage));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask),
                                ContextHelper.getString(R.string.app_name),
                                ContextHelper.getString(R.string.storage));
                    case MESSAGE_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.this_lets_use_storage_mask),
                                ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String
                            .format(ContextHelper.getString(R.string.this_lets_use_storage_mask),
                                ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper
                                .getString(
                                    R.string.nf_to_enable_this_click_below_and_activate_permission_mask),
                            ContextHelper.getString(R.string.app_settings),
                            ContextHelper.getString(R.string.storage)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsStorage();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsStorage(nsPermission);
            }
        };
    }

    @Override
    protected PermissionCamera createPermissionCamera() {
        return new PermissionCamera() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask),
                            ContextHelper.getString(R.string.camera));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask),
                                ContextHelper.getString(R.string.app_name),
                                ContextHelper.getString(R.string.camera));
                    case MESSAGE_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.that_lets_use_camera_mask),
                                ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String
                            .format(ContextHelper.getString(R.string.that_lets_use_camera_mask),
                                ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(
                            R.string.nf_to_enable_this_click_below_and_activate_permission_mask),
                            ContextHelper.getString(R.string.app_settings),
                            ContextHelper.getString(R.string.camera)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsCamera();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsCamera(nsPermission);
            }
        };
    }

    @Override
    protected PermissionRecordAudio createPermissionRecordAudio() {
        return new PermissionRecordAudio() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask),
                            ContextHelper.getString(R.string.microphone));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask),
                                ContextHelper.getString(R.string.app_name),
                                ContextHelper.getString(R.string.microphone));
                    case MESSAGE_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.this_lets_use_mic_mask),
                                ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String
                            .format(ContextHelper.getString(R.string.this_lets_use_mic_mask),
                                ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper
                                .getString(
                                    R.string.nf_to_enable_this_click_below_and_activate_permission_mask),
                            ContextHelper.getString(R.string.app_settings),
                            ContextHelper.getString(R.string.microphone)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsRecAudio();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsRecAudio(nsPermission);
            }
        };
    }

    @Override
    protected PermissionAccounts createPermissionAccounts() {
        return new PermissionAccounts() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask),
                            ContextHelper.getString(R.string.accounts));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask),
                                ContextHelper.getString(R.string.app_name),
                                ContextHelper.getString(R.string.accounts));
                    case MESSAGE_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.this_lets_use_contacts_mask),
                                ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String
                            .format(ContextHelper.getString(R.string.this_lets_use_contacts_mask),
                                ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper
                                .getString(
                                    R.string.nf_to_enable_this_click_below_and_activate_permission_mask),
                            ContextHelper.getString(R.string.app_settings),
                            ContextHelper.getString(R.string.accounts)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsAccounts();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsAccounts(nsPermission);
            }
        };
    }

    @Override
    protected PermissionReadContacts createPermissionReadContacts() {
        return new PermissionReadContacts() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask),
                            ContextHelper.getString(R.string.contacts));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String
                            .format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask),
                                ContextHelper.getString(R.string.app_name),
                                ContextHelper.getString(R.string.contacts));
                    case MESSAGE_RATIONALE:
                        return String.format(
                            ContextHelper.getString(R.string.this_lets_use_phone_contacts_mask),
                            ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(
                            ContextHelper.getString(R.string.this_lets_use_phone_contacts_mask),
                            ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(
                            R.string.nf_to_enable_this_click_below_and_activate_permission_mask),
                            ContextHelper.getString(R.string.app_settings),
                            ContextHelper.getString(R.string.contacts)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsReadContacts();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsReadContacts(nsPermission);
            }
        };
    }

    @Override
    protected PermissionSMS createPermissionSMS() {
        return new PermissionSMS() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask), ContextHelper.getString(R.string.sms));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask), ContextHelper.getString(R.string.app_name), ContextHelper.getString(R.string.sms));
                    case MESSAGE_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.this_lets_use_sms_mask), ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(ContextHelper.getString(R.string.this_lets_use_sms_mask), ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(R.string.nf_to_enable_this_click_below_and_activate_permission_mask), ContextHelper.getString(R.string.app_settings), ContextHelper.getString(R.string.sms)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsSMS();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsSMS(nsPermission);
            }
        };
    }

    @Override
    protected PermissionLocation createPermissionLocation() {
        return new PermissionLocation() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask), ContextHelper.getString(R.string.location));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask), ContextHelper.getString(R.string.app_name), ContextHelper.getString(R.string.location));
                    case MESSAGE_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.this_lets_use_location_mask), ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(ContextHelper.getString(R.string.this_lets_use_location_mask), ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(R.string.nf_to_enable_this_click_below_and_activate_permission_mask), ContextHelper.getString(R.string.app_settings), ContextHelper.getString(R.string.location)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsLocation();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsLocation(nsPermission);
            }
        };
    }

    protected Permission createPermissionGPS() {
        return new PermissionGPS() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask), ContextHelper.getString(R.string.gps));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask), ContextHelper.getString(R.string.app_name), ContextHelper.getString(R.string.gps));
                    case MESSAGE_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.this_lets_use_gps_mask), ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(ContextHelper.getString(R.string.this_lets_use_gps_mask), ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(R.string.nf_to_enable_this_click_below_and_activate_permission_mask), ContextHelper.getString(R.string.app_settings), ContextHelper.getString(R.string.gps)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsGps();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsGps(nsPermission);
            }
        };
    }

    @Override
    protected Permission createPermissionPhone() {
        return new PermissionPhone() {
            @Override
            protected String getText(EPermissionTexts text) {
                switch (text) {
                    case ALLOW:
                        return ContextHelper.getString(R.string.allow);
                    case DENY:
                        return ContextHelper.getString(R.string.deny);
                    case NOT_NOW:
                        return ContextHelper.getString(R.string.not_now);
                    case APP_SETTINGS:
                        return ContextHelper.getString(R.string.app_settings);
                    case TOAST_DENIED:
                        return String.format(ContextHelper.getString(R.string.denied_access_mask), ContextHelper.getString(R.string.phone));
                    case HEADER_BLOCKED:
                    case HEADER_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.nf_allow_to_access_your_mask), ContextHelper.getString(R.string.app_name), ContextHelper.getString(R.string.phone));
                    case MESSAGE_RATIONALE:
                        return String.format(ContextHelper.getString(R.string.this_lets_use_phone_mask), ContextHelper.getString(R.string.app_name));
                    case MESSAGE_BLOCKED:
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format(ContextHelper.getString(R.string.this_lets_use_phone_mask), ContextHelper.getString(R.string.app_name)));
                        builder.append("\n\n");
                        builder.append(String.format(ContextHelper.getString(R.string.nf_to_enable_this_click_below_and_activate_permission_mask), ContextHelper.getString(R.string.app_settings), ContextHelper.getString(R.string.phone)));
                        return builder.toString();
                }
                return "";
            }

            @Override
            protected boolean isPermissionDoNotShowAgain() {
                return AppConfig.INSTANCE.getSettingsPermissions().isNsPhone();
            }

            @Override
            protected void setPermissionDoNotShowAgain(boolean nsPermission) {
                AppConfig.INSTANCE.getSettingsPermissions().setNsPhone(nsPermission);
            }
        };
    }
}
