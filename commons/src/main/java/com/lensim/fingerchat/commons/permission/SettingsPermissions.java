package com.lensim.fingerchat.commons.permission;


import com.lensim.fingerchat.commons.app.BaseConfig;
import com.lensim.fingerchat.commons.helper.ContextHelper;

public class SettingsPermissions extends BaseConfig {
    private final static String SETTINGS_PERMISSION_KEY = "settings.permissions";

    // region Static
    private final static String NsCamera = "nsCamera";
    private final static String NsStorage = "nsStorage";
    private final static String NsRecAudio = "nsRecAudio";
    private final static String NsContacts = "nsContacts";
    private final static String NsReadContacts = "nsReadContacts";
    private final static String NsSMS = "nsSMS";
    private final static String NsPhone = "nsPhone";
    private final static String NsGPS = "nsGps";
    private final static String NsLocation = "nsLocation";
    // endregion

    // region _fields
    private boolean _nsStorage;
    private boolean _nsCamera;
    private boolean _nsRecAudio;
    private boolean _nsAccounts;
    private boolean _nsReadContacts;
    private boolean _nsSMS;
    private boolean _nsPhone;
    private boolean _nsGps;
    private boolean _nsLocation;
    // endregion

    // region Constructor
    public SettingsPermissions() {
        super(ContextHelper.getContext(), SETTINGS_PERMISSION_KEY);
    }
    // endregion

    // region Properties


    public boolean isNsAccounts() {
        return _nsAccounts;
    }

    public void setNsAccounts(boolean nsAccounts) {
        _nsAccounts = nsAccounts;
        put(NsContacts, _nsAccounts);
    }

    public boolean isNsReadContacts() {
        return _nsReadContacts;
    }

    public void setNsReadContacts(boolean nsReadContacts) {
        _nsReadContacts = nsReadContacts;
        put(NsReadContacts, _nsReadContacts);
    }

    public boolean isNsLocation() {
        return _nsLocation;
    }

    public void setNsLocation(boolean nsLocation) {
        _nsLocation = nsLocation;
        put(NsLocation, _nsLocation);
    }

    public boolean isNsRecAudio() {
        return _nsRecAudio;
    }

    public void setNsRecAudio(boolean nsRecAudio) {
        _nsRecAudio = nsRecAudio;
        put(NsRecAudio, _nsRecAudio);
    }

    public boolean isNsStorage() {
        return _nsStorage;
    }

    public void setNsStorage(boolean nsStorage) {
        _nsStorage = nsStorage;
        put(NsStorage, _nsStorage);
    }

    public boolean isNsCamera() {
        return _nsCamera;
    }

    public void setNsCamera(boolean nsCamera) {
        _nsCamera = nsCamera;
        put(NsCamera, _nsCamera);
    }

    public boolean isNsSMS() {
        return _nsSMS;
    }

    public void setNsSMS(boolean nsSMS) {
        _nsSMS = nsSMS;
        put(NsSMS, _nsSMS);
    }

    public boolean isNsPhone() {
        return _nsPhone;
    }

    public void setNsPhone(boolean nsPhone) {
        _nsPhone = nsPhone;
        put(NsPhone, _nsPhone);
    }

    public boolean isNsGps() {
        return _nsGps;
    }

    public void setNsGps(boolean nsGps) {
        _nsGps = nsGps;
        put(NsGPS, _nsGps);
    }

    // endregion

    // region Methods
    public void load() {
        _nsCamera = get(NsCamera, false);
        _nsStorage = get(NsStorage, false);
        _nsRecAudio = get(NsRecAudio, false);
        _nsAccounts = get(NsContacts, false);
        _nsReadContacts = get(NsReadContacts, false);
        _nsSMS = get(NsSMS, false);
        _nsPhone = get(NsPhone, false);
        _nsGps = get(NsGPS, false);
        _nsLocation = get(NsLocation, false);
    }

    public void save() {
        put(NsCamera, _nsCamera);
        put(NsStorage, _nsStorage);
        put(NsRecAudio, _nsRecAudio);
        put(NsContacts, _nsAccounts);
        put(NsReadContacts, _nsReadContacts);
        put(NsSMS, _nsSMS);
        put(NsPhone, _nsPhone);
        put(NsGPS, _nsGps);
        put(NsLocation, _nsLocation);
    }
    // endregion
}
