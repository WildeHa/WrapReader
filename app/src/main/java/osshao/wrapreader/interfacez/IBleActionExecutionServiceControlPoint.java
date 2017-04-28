package osshao.wrapreader.interfacez;

import java.util.List;

/**
 * Created by LSCM on 2017/4/28.
 */

public interface IBleActionExecutionServiceControlPoint {

    boolean addToQueue(IBleAction bleAction);

    boolean addToQueue(List<IBleAction> bleActions);

}
