package org.matsim.training.session3;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

public class RunMatsimManchester {

    // Scale factor (must be same as specified when generating demand)
    private static final double SCALE_FACTOR = 0.01;

    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        config.controler().setLastIteration(5);
        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

        // Specify input network and plans files
        config.network().setInputFile("input/network/greater-manchester.xml");
        config.plans().setInputFile("input/plans.xml");

        // Specify scale factor
        config.qsim().setFlowCapFactor(SCALE_FACTOR);
        config.qsim().setStorageCapFactor(SCALE_FACTOR);

        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
        home.setTypicalDuration(16 * 60 * 60);
        config.planCalcScore().addActivityParams(home);
        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
        work.setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(work);

        // define strategies:
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
            strat.setWeight(0.15);
            config.strategy().addStrategySettings(strat);
        }
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
            strat.setWeight(0.9);

            config.strategy().addStrategySettings(strat);
        }
        config.strategy().setFractionOfIterationsToDisableInnovation(0.9);

        config.vspExperimental().setWritingOutputEvents(true);

        Scenario scenario = ScenarioUtils.loadScenario(config) ;


        Controler controler = new Controler( scenario ) ;
        controler.run();

    }

}
