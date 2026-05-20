window.ActiveTheme = {
    // Expose settings to the Engine's right drawer
    settingsConfig: {
        zoom: {
            label: "Size Scale",
            type: "range",
            min: 0.5,
            max: 1.5,
            step: 0.1,
            default: 1.0,
            displaySuffix: "x"
        },
        hue: {
            label: "Aura Color",
            type: "palette",
            // Array of raw HSL hue numbers: Blue, Purple, Rose, Orange, Green
            options: [210, 270, 330, 30, 150], 
            default: 210
        },
        glow: {
            label: "Glow Intensity",
            type: "range",
            min: 5,
            max: 40,
            step: 5,
            default: 20
        },
        showText: {
            label: "Center Digital Clock",
            type: "select",
            options: [
                { value: "0", text: "Hidden (Zen Mode)" },
                { value: "1", text: "Visible" }
            ],
            default: "0"
        }
    },

    elements: {},

    init: function (stage, savedSettings) {
        this.stage = stage;
        this.stage.innerHTML = `
            <div id="harmonic-container">
                <!-- Hours -->
                <div class="orbit-ring orbit-hours">
                    <div class="rotator" id="rot-h">
                        <div class="orb-planet planet-hours"></div>
                    </div>
                </div>
                <!-- Minutes -->
                <div class="orbit-ring orbit-minutes">
                    <div class="rotator" id="rot-m">
                        <div class="orb-planet planet-minutes"></div>
                    </div>
                </div>
                <!-- Seconds -->
                <div class="orbit-ring orbit-seconds">
                    <div class="rotator" id="rot-s">
                        <div class="orb-planet planet-seconds"></div>
                    </div>
                </div>
                <!-- Center Core -->
                <div class="orb-center">
                    <div class="orb-time-text" id="orb-text">00:00</div>
                </div>
            </div>
        `;

        this.elements = {
            container: document.getElementById('harmonic-container'),
            rotH: document.getElementById('rot-h'),
            rotM: document.getElementById('rot-m'),
            rotS: document.getElementById('rot-s'),
            text: document.getElementById('orb-text')
        };

        // Apply defaults or saved settings
        const config = this.settingsConfig;
        this.onSettingsChange('zoom', savedSettings.zoom || config.zoom.default);
        this.onSettingsChange('hue', savedSettings.hue || config.hue.default);
        this.onSettingsChange('glow', savedSettings.glow || config.glow.default);
        this.onSettingsChange('showText', savedSettings.showText || config.showText.default);
    },

    update: function (time) {
        // Parse time from strings to numbers
        const h = parseInt(time.h, 10);
        const m = parseInt(time.m, 10);
        const s = parseInt(time.s, 10);

        // Mathematical conversions for smooth rotation
        // Seconds: $s * 6$ degrees
        const degS = s * 6;
        // Minutes: $m * 6 + s * 0.1$ degrees (moves smoothly as seconds tick)
        const degM = (m * 6) + (s * 0.1);
        // Hours: $(h % 12) * 30 + m * 0.5$ degrees
        const degH = ((h % 12) * 30) + (m * 0.5);

        // Apply rotations
        this.elements.rotH.style.transform = `rotate(${degH}deg)`;
        this.elements.rotM.style.transform = `rotate(${degM}deg)`;
        this.elements.rotS.style.transform = `rotate(${degS}deg)`;

        // Update optional center text
        this.elements.text.innerText = `${time.h}:${time.m}`;
    },

    onSettingsChange: function (key, value) {
        if (!this.elements.container) return;

        // Use CSS variables attached to the container to drive the styling
        if (key === 'zoom') {
            this.elements.container.style.setProperty('--orb-zoom', value);
        } else if (key === 'hue') {
            document.body.style.setProperty('--orb-hue', value); // Body handles bg gradient
            this.elements.container.style.setProperty('--orb-hue', value);
        } else if (key === 'glow') {
            this.elements.container.style.setProperty('--orb-glow', value);
        } else if (key === 'showText') {
            this.elements.container.style.setProperty('--text-opacity', value);
        }
    },

    destroy: function () {
        this.stage.innerHTML = '';
        document.body.style.removeProperty('--orb-hue');
    }
};
