package co.merkhet.ttt.demo;

public class TTTActionSpaceImpl extends TTTActionSpace {

  protected TTTActionSpaceImpl(String... actions) {
    super(actions.length);
    this.actions = actions;
  }

}
