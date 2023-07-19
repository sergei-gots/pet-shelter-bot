package pro.sky.petshelterbot.constants;

public interface TelegramChatStates {
    enum ChatState {
        INITIAL_STATE,
        ADOPTER_CHOICES_SHELTER,

        ADOPTER_IN_SHELTER_INFO_MENU,
        ADOPTER_IN_ADOPTION_INFO_MENU,

        MENU_NAVIGATION,
        ADOPTER_DIALOG,
        /** Not a state, just a mask for chatState.name().startsWith(ADOPTER_INPUTS.name()) **/
        ADOPTER_INPUTS,
        ADOPTER_INPUTS_CONTACTS,
        ADOPTER_INPUTS_PHONE_NUMBER,
        ADOPTER_INPUTS_REPORT_DIET,
        ADOPTER_INPUTS_REPORT_WELL_BEING,
        ADOPTER_INPUTS_REPORT_BEHAVIOUR,
        ADOPTER_INPUTS_REPORT_IMAGE
    }

    default boolean isAdopterInInputState(ChatState chatState) {
        return chatState.name().startsWith(ChatState.ADOPTER_INPUTS.name());
    }
}
