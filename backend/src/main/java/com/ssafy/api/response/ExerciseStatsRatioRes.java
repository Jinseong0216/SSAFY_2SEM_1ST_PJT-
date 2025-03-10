package com.ssafy.api.response;

import com.ssafy.db.entity.ExerciseStatsRatio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("ExerciseStatsRatioRes")
public class ExerciseStatsRatioRes {
    @ApiModelProperty(name = "exerciseStatsRatioId")
    private Long id;
    @ApiModelProperty(name="exercise_type")
    private String exerciseType;
    @ApiModelProperty(name="chest_ratio")
    private Float chestRatio;
    @ApiModelProperty(name="back_ratio")
    private Float backRatio;
    @ApiModelProperty(name="stamina_ratio")
    private Float staminaRatio;
    @ApiModelProperty(name="arms_ratio")
    private Float armsRatio;
    @ApiModelProperty(name="legs_ratio")
    private Float legsRatio;
    @ApiModelProperty(name="abs_ratio")
    private Float absRatio;

    public static ExerciseStatsRatioRes of(ExerciseStatsRatio entity) {
        ExerciseStatsRatioRes res = new ExerciseStatsRatioRes();
        res.setId(entity.getId());
        res.setExerciseType(entity.getExerciseType());
        res.setChestRatio(entity.getChestRatio());
        res.setBackRatio(entity.getBackRatio());
        res.setStaminaRatio(entity.getStaminaRatio());
        res.setArmsRatio(entity.getArmsRatio());
        res.setLegsRatio(entity.getLegsRatio());
        res.setAbsRatio(entity.getAbsRatio());
        return res;
    }
}
