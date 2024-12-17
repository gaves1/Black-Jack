import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class blackjack {
    // Inner class to represent a Card with value and type (suit)
    private class Card {
        String value; // Card value (A, 2-10, J, Q, K)
        String type; // Card suit (C, D, H, S)

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        // Returns a string representation of the card
        public String toString() {
            return value + "-" + type;
        }

        // Returns the numeric value of the card
        public int getValue() {
            if ("AJQK".contains(value)) { // Special values for A, J, Q, K
                if (value == "A") {
                    return 11; // Ace initially count as 11
                }
                return 10; // J, Q, K count as 10
            }
            return Integer.parseInt(value); // Numeric cards (2-10)
        }

        // Checks if the card is an Ace
        public boolean isAce() {
            return value == "A";
        }

        // Returns the image path for the card
        public String getImagePath() {
            return  "./cards/" + toString() + ".png";
        }
    }

            // Variables for game state
            ArrayList<Card> deck; // The deck of cards
            Random random = new Random(); // Random generator for shuffling

            // Dealer variables
            Card hiddenCard; // The dealer´s hidden card
            ArrayList<Card> dealerHand; // Dealer´s hand
            int dealerSum; // Total value of the dealer´s hand
            int dealerAceCount; // Count of Aces in dealer´s hand

            // Player variables
            ArrayList<Card> playerHand; // Player´s hand
            int playerSum; // Total value of player´s hand
            int playerAceCount; // Count of Aces in player´s hand

            // UI variables
            int boardwidth = 600; // Width of the game window
            int boardHeight = boardwidth; // Height of the game Window
            int cardWidth = 110; // Width of each card
            int cardHeight = 154; // Height of e ach card

            JFrame frame = new JFrame("Black Jack"); // Main Game window
            JPanel gamePanel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    try {
                        // Draw the dealer´s hidden card
                        Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                        if (!stayButton.isEnabled()) {
                            // Reveal the hidden card if Stay button is disabled
                            hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                        }
                        g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                        // Draw the dealer´s visible cards
                        for (int i = 0; i < dealerHand.size(); i++) {
                            Card card = dealerHand.get(i);
                            Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                            g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                        }

                        // Draw the player´s cards
                        for (int i = 0; i < playerHand.size(); i++) {
                            Card card = playerHand.get(i);
                            Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                            g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);

                        }

                        // Display game result when Stay button is disabled
                        if (!stayButton.isEnabled()) {
                            dealerSum = reduceDealerAce();
                            playerSum = reducePlayerAce();
                            System.out.println("STAY: ");
                            System.out.println(dealerSum);
                            System.out.println(playerSum);

                            String message = "";
                            if (playerSum > 21) {
                                message = "You Lose!";
                            }
                            else if (dealerSum > 21 ) {
                                message = "You Win!";
                            }
                            //both you and dealer <= 21
                            else if (playerSum == dealerSum) {
                                message = "Tie!";
                            }
                            else if (playerSum < dealerSum) {
                                message = "You Lose!";
                            }
                            else if (playerSum > dealerSum) {
                                message = "You Win!";
                            }

                            g.setFont(new Font("Arial", Font.PLAIN, 30));
                            g.setColor(Color.WHITE);
                            g.drawString(message, 220, 250);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            JPanel buttonPanel = new JPanel(); // Panel for Buttons
            JButton hitButton = new JButton("Hit"); // Hit button
            JButton stayButton = new JButton("Stay"); // Stay button
            JButton playAgainButton = new JButton("Play Again"); // Play Again button

            // Constructor to initialize the game
            blackjack() {
                startGame();

                // Configure main frame
                frame.setVisible(true);
                frame.setSize(boardwidth, boardHeight);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Configure game panel
                gamePanel.setLayout(new BorderLayout());
                gamePanel.setBackground(new Color(53, 101, 77));
                frame.add(gamePanel);

                // Configure buttons
                hitButton.setFocusable(false);
                buttonPanel.add(hitButton);
                stayButton.setFocusable(false);
                buttonPanel.add(stayButton);
                playAgainButton.setFocusable(false);
                buttonPanel.add(playAgainButton);
                frame.add(buttonPanel, BorderLayout.SOUTH);

                // Add ActionListener for Hit button
                hitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Card card = deck.remove(deck.size()-1);
                        playerSum += card.getValue();
                        playerAceCount += card.isAce()? 1 : 0;
                        playerHand.add(card);

                        // Check if player busts
                        if (reducePlayerAce() > 21) { // Check if the player is busted
                            hitButton.setEnabled(false);
                            stayButton.setEnabled(false); // Disable Stay Button too
                            gamePanel.repaint();// Trigger repaint to display the "You Lose!" message
                        }
                        gamePanel.repaint(); // Update visuals after taking a card
                    }
                });

                // Add ActionListener for stay button
                stayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // End player´s turn and let dealer play
                        hitButton.setEnabled(false);
                        stayButton.setEnabled(false);

                        while (dealerSum < 17) {
                            Card card = deck.remove(deck.size()-1);
                            dealerSum += card.getValue();
                            dealerAceCount += card.isAce()? 1 : 0;
                            dealerHand.add(card);
                        }
                        gamePanel.repaint(); // Update visuals after dealer´s turn
                    }
                });

                // Add ActionListener for play Again button
                playAgainButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (stayButton.isEnabled() || hitButton.isEnabled()) {
                            JOptionPane.showMessageDialog(frame, "Finis your current game First!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else {
                            startGame(); // Start a new game
                            hitButton.setEnabled(true);
                            stayButton.setEnabled(true);
                            gamePanel.repaint();
                        }
                    }
                });

                gamePanel.repaint(); // Initial paint
            }

            // Starts a new game by initializing deck and hands
            public void startGame() {
                buildDeck(); // Create a new deck
                shuffleDeck(); // Shuffle the deck

                // Initialize dealer´s hand
                dealerHand = new ArrayList<Card>();
                dealerSum = 0;
                dealerAceCount = 0;

                hiddenCard = deck.remove(deck.size()-1); // Dealer´s hidden card
                dealerSum += hiddenCard.getValue();
                dealerAceCount += hiddenCard.isAce() ? 1 : 0;

                Card card = deck.remove(deck.size()-1);
                dealerSum += card.getValue();
                dealerAceCount += card.isAce() ? 1 : 0;
                dealerHand.add(card);

                // Initialize player´s hand
                playerHand = new ArrayList<Card>();
                playerSum = 0;
                playerAceCount = 0;

                for (int i = 0; i < 2; i++) {
                    card = deck.remove(deck.size()-1);
                    playerSum += card.getValue();
                    playerAceCount += card.isAce() ? 1 : 0;
                    playerHand.add(card);
                }
            }

            // Builds a standard deck of 52 cards
            public void buildDeck() {
                deck = new ArrayList<Card>();
                String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
                String[] types = {"C", "D", "H", "S"}; // Clubs, Diamonds, Hearts, Spades

                for (String type : types) {
                    for (String value : values) {
                        Card card = new Card(value, type);
                        deck.add(card);
                    }
                }

                System.out.println("BUILD DECK:");
                System.out.println(deck);
            }

            // Shuffles the deck using a random swap algorithm
            public void shuffleDeck() {
                for (int i = 0; i < deck.size(); i++) {
                    int j = random.nextInt(deck.size());
                    Card currCard = deck.get(i);
                    Card randomCard = deck.get(j);
                    deck.set(i, randomCard);
                    deck.set(j, currCard);
                }
            }

            // Adjusts player´s Ace values to avoid busting
            public int reducePlayerAce() {
                while (playerSum > 21 && playerAceCount > 0) {
                    playerSum -= 10; // Convert Ace from 11 to 1
                    playerAceCount--;
                }
                return playerSum;
            }

            // Adjusts dealer´s Ace values to avoid busting
            public int reduceDealerAce() {
                while (dealerSum > 21 && dealerAceCount > 0) {
                    dealerSum -= 10;
                    dealerAceCount -= 1;
                }
                return dealerSum;
            }
}
