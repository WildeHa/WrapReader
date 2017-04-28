package osshao.wrapreader;

import java.util.UUID;

import osshao.wrapreader.interfacez.IBleProvider;
import osshao.wrapreader.model.UserProfile;

/**
 * Created by LSCM on 2017/4/28.
 */

public class SetUserProfileAction extends WriteAction {

    protected SetUserProfileAction(UserProfile userProfile, String deviceAddress) {
        super(Constants.services.UUID_SERVICE_MILI, Constants.charateristics.UUID_CHAR_USER_INFO);

        if (userProfile == null) {
            throw new NullPointerException("UserProfile is null");
        }

        super.data = userProfile.getBytes(deviceAddress);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
