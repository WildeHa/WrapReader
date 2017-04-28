package osshao.wrapreader.interfacez;

/**
 * Created by LSCM on 2017/4/28.
 */

public interface IUserProfile {
    int getUid();

    byte getGender();

    byte getAge();

    int getHeight();

    int getWeight();

    String getAlias();

    byte getType();

    byte[] getBytes(String address);
}
