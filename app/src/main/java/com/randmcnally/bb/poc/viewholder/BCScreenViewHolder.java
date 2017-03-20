package com.randmcnally.bb.poc.viewholder;

public class BCScreenViewHolder {
    /*
    private ImageView image;
    private TextView text;
    private State BCState;

    public BCScreenViewHolder(View view) {
        image = (ImageView) view.findViewById(R.id.channel_img_microphone);
        text = (TextView) view.findViewById(R.id.channel_txt_state);
    }

    public void setState(State state) {
        this.BCState = state;
        image.setImageResource(BCState.getImageRes());
        text.setText(BCState.getText());

    }

    public static enum State {
        LOADING {
            @Override
            public int getImageRes() {
                return R.drawable.xyz;
            }

            @Override
            public int getText() {
                return R.string.ready;
            }

            @Override
            public int getBackgroudColor() {
                return R.color.xyz;
            }
        },
        READY {

        },
        RECEIVING {

        },
        BROADCASTING {

        };

        public abstract
        @DrawableRes
        int getImageRes();

        public abstract
        @StringRes
        int getText();

        public abstract
        @ColorRes
        int getBackgroudColor();

    }
    */
}