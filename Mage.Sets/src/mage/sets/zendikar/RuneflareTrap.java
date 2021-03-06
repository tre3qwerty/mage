/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.zendikar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.abilities.costs.AlternativeCostSourceAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.WatcherScope;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;
import mage.target.TargetPlayer;
import mage.watchers.Watcher;

/**
 *
 * @author jeffwadsworth
 */
public class RuneflareTrap extends CardImpl {

    public RuneflareTrap(UUID ownerId) {
        super(ownerId, 146, "Runeflare Trap", Rarity.UNCOMMON, new CardType[]{CardType.INSTANT}, "{4}{R}{R}");
        this.expansionSetCode = "ZEN";
        this.subtype.add("Trap");

        // If an opponent drew three or more cards this turn, you may pay {R} rather than pay Runeflare Trap's mana cost.
        this.addAbility(new AlternativeCostSourceAbility(new ManaCostsImpl("{R}"), RavenousTrapCondition.getInstance()), new CardsDrawnWatcher());

        // Runeflare Trap deals damage to target player equal to the number of cards in that player's hand.
        this.getSpellAbility().addEffect(new DamageTargetEffect(new TargetPlayerCardsInHandCount()));
        this.getSpellAbility().addTarget(new TargetPlayer());

    }

    public RuneflareTrap(final RuneflareTrap card) {
        super(card);
    }

    @Override
    public RuneflareTrap copy() {
        return new RuneflareTrap(this);
    }
}

class TargetPlayerCardsInHandCount implements DynamicValue {

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        Player targetPlayer = game.getPlayer(sourceAbility.getFirstTarget());
        if (targetPlayer != null) {
            return targetPlayer.getHand().size();
        }

        return 0;
    }

    @Override
    public DynamicValue copy() {
        return new TargetPlayerCardsInHandCount();
    }

    @Override
    public String toString() {
        return "1";
    }

    @Override
    public String getMessage() {
        return "target player's cards in hand";
    }
}

class RuneflareTrapCondition implements Condition {

    private static final RuneflareTrapCondition fInstance = new RuneflareTrapCondition();

    public static Condition getInstance() {
        return fInstance;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        CardsDrawnWatcher watcher = (CardsDrawnWatcher) game.getState().getWatchers().get("CardsDrawnWatcher");
        return watcher != null && watcher.opponentDrewXOrMoreCards(source.getControllerId(), 3, game);
    }

    @Override
    public String toString() {
        return "If an opponent drew three or more cards this turn";
    }
}

class CardsDrawnWatcher extends Watcher {

    private final Map<UUID, Integer> playerCardsDrawn = new HashMap<>();

    public CardsDrawnWatcher() {
        super("CardsDrawnWatcher", WatcherScope.GAME);
    }

    public CardsDrawnWatcher(final CardsDrawnWatcher watcher) {
        super(watcher);
        playerCardsDrawn.putAll(watcher.playerCardsDrawn);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.DREW_CARD) {
            if (playerCardsDrawn.containsKey(event.getPlayerId())) {
                playerCardsDrawn.put(event.getPlayerId(), playerCardsDrawn.get(event.getPlayerId()) + 1);
            } else {
                playerCardsDrawn.put(event.getPlayerId(), 1);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        playerCardsDrawn.clear();
    }

    public boolean opponentDrewXOrMoreCards(UUID playerId, int x, Game game) {
        Player player = game.getPlayer(playerId);
        if (player != null) {
            for (Map.Entry<UUID, Integer> entry : playerCardsDrawn.entrySet()) {
                if (game.isOpponent(player, playerId)) {
                    if (entry.getValue() >= x) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public CardsDrawnWatcher copy() {
        return new CardsDrawnWatcher(this);
    }
}
