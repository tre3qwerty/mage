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
package mage.sets.bornofthegods;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.Target;
import mage.target.TargetPermanent;

/**
 *
 * @author LevelX2
 */
public class UnravelTheAEther extends CardImpl {

    private static final FilterPermanent filter = new FilterPermanent();

    static {
        filter.add(Predicates.or(new CardTypePredicate(CardType.ARTIFACT),
                new CardTypePredicate(CardType.ENCHANTMENT)));
    }

    public UnravelTheAEther(UUID ownerId) {
        super(ownerId, 143, "Unravel the AEther", Rarity.UNCOMMON, new CardType[]{CardType.INSTANT}, "{1}{G}");
        this.expansionSetCode = "BNG";

        // Choose target artifact or enchantment. Its owner shuffles it into his or her library.
        this.getSpellAbility().addEffect(new UnravelTheAEtherShuffleIntoLibraryEffect());
        Target target = new TargetPermanent(1, 1, filter, false);
        this.getSpellAbility().addTarget(target);
    }

    public UnravelTheAEther(final UnravelTheAEther card) {
        super(card);
    }

    @Override
    public UnravelTheAEther copy() {
        return new UnravelTheAEther(this);
    }
}

class UnravelTheAEtherShuffleIntoLibraryEffect extends OneShotEffect {

    public UnravelTheAEtherShuffleIntoLibraryEffect() {
        super(Outcome.Detriment);
        this.staticText = "Choose target artifact or enchantment. Its owner shuffles it into his or her library";
    }

    public UnravelTheAEtherShuffleIntoLibraryEffect(final UnravelTheAEtherShuffleIntoLibraryEffect effect) {
        super(effect);
    }

    @Override
    public UnravelTheAEtherShuffleIntoLibraryEffect copy() {
        return new UnravelTheAEtherShuffleIntoLibraryEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(targetPointer.getFirst(game, source));
        if (permanent != null) {
            if (permanent.moveToZone(Zone.LIBRARY, source.getSourceId(), game, true)) {
                game.getPlayer(permanent.getOwnerId()).shuffleLibrary(source, game);
                return true;
            }
        }
        return false;
    }
}
