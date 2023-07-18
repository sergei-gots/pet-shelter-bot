package pro.sky.petshelterbot.constants;

public interface TelegramChatStates {
    enum ChatState {
        MENU_NAVIGATION,
        /** Not a state, just a mask for chatState.name().startsWith(ADOPTER_INPUTS.name()) **/
        ADOPTER_INPUTS,
        ADOPTER_INPUTS_CONTACTS,
        ADOPTER_INPUTS_REPORT_DIET,
        ADOPTER_INPUTS_REPORT_WELL_BEING,
        ADOPTER_INPUTS_REPORT_BEHAVIOUR,
        ADOPTER_INPUTS_REPORT_IMAGE
    }

    default boolean isAdopterInputState(ChatState chatState) {
        return chatState.name().startsWith(ChatState.ADOPTER_INPUTS.name());
    }
}
